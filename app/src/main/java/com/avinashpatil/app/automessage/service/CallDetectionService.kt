package com.avinashpatil.app.automessage.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.app.PendingIntent
import android.app.AlarmManager
import com.avinashpatil.app.automessage.receiver.KeepAliveReceiver
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.Context
import androidx.core.app.NotificationCompat
import android.graphics.BitmapFactory
import com.avinashpatil.app.automessage.R
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository
import com.avinashpatil.app.automessage.data.repository.ContactRepository
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository
import com.avinashpatil.app.automessage.data.repository.GroupRepository
import com.avinashpatil.app.automessage.data.repository.MessageRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.annotation.SuppressLint
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.os.PowerManager
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import android.os.Looper
import android.os.Handler
import android.widget.Toast

@AndroidEntryPoint
class CallDetectionService : Service() {
    @Inject lateinit var dataStoreRepository: DataStoreRepository
    @Inject lateinit var contactRepository: ContactRepository
    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var autoReplyRepository: AutoReplyRepository
    @Inject lateinit var groupRepository: GroupRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    // Prevent duplicate sends across OFFHOOK/IDLE/outgoing flows
    private val recentlySent = ConcurrentHashMap<String, Long>()
    private val answeredNumbers = ConcurrentHashMap<String, Long>()
    private val callTypeByNumber = ConcurrentHashMap<String, String>()
    private val lastOutgoingDialed = ConcurrentHashMap<String, Long>()
    private lateinit var tm: TelephonyManager
    private var callStateListener: PhoneStateListener? = null
    private var callStateCallback: TelephonyCallback? = null
    private var callLogObserver: android.database.ContentObserver? = null

    private fun hasSentRecently(phoneNumber: String, windowMs: Long = 30_000): Boolean {
        val ctx = this@CallDetectionService
        val prefRecent = com.avinashpatil.app.automessage.utils.DuplicatePreventer.hasSentRecently(ctx, phoneNumber, windowMs)
        if (prefRecent) return true
        val last = recentlySent[phoneNumber] ?: return false
        return System.currentTimeMillis() - last < windowMs
    }

    private fun numbersMatch(a: String?, b: String?): Boolean {
        if (a.isNullOrBlank() || b.isNullOrBlank()) return false
        return try {
            matchByTailDigits(a, b)
        } catch (_: Exception) {
            matchByTailDigits(a, b)
        }
    }

    private fun matchByTailDigits(a: String, b: String): Boolean {
        val na = a.filter { it.isDigit() }
        val nb = b.filter { it.isDigit() }
        val min = minOf(7, minOf(na.length, nb.length))
        if (min <= 0) return false
        return na.takeLast(min) == nb.takeLast(min)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "call_detection_channel"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_SMS_SENT = "com.avinashpatil.app.automessage.SMS_SENT"
        private const val ACTION_SMS_DELIVERED = "com.avinashpatil.app.automessage.SMS_DELIVERED"
        private const val TAG = "CallDetectionService"
        private const val ACTION_KEEPALIVE = "com.avinashpatil.app.automessage.ACTION_KEEPALIVE"
    }

    private fun openSmsAppFallback(phoneNumber: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = android.net.Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            try {
                val nm = getSystemService(NotificationManager::class.java)
                val n = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Manual send required")
                    .setContentText("Tap to send SMS to $phoneNumber")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()
                nm.notify(1003, n)
            } catch (_: Exception) { }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to open SMS app fallback", e)
        }
    }

    private val smsSentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            val logId = intent?.getLongExtra("log_id", -1L) ?: -1L
            val attempts = intent?.getIntExtra("attempts", 1) ?: 1
            val phone = intent?.getStringExtra("phone") ?: ""
            val result = resultCode
            serviceScope.launch {
                when (result) {
                    android.app.Activity.RESULT_OK -> {
                        autoReplyRepository.markLogSent(id = logId, attempts = attempts, sentTs = System.currentTimeMillis())
                        try { com.avinashpatil.app.automessage.utils.DuplicatePreventer.markSent(this@CallDetectionService, phone) } catch (_: Exception) {}
                        try { com.avinashpatil.app.automessage.utils.DailyMessageTracker.markSentToday(this@CallDetectionService, phone) } catch (_: Exception) {}
                        try { autoReplyRepository.markAllAsDelivered(System.currentTimeMillis()) } catch (_: Exception) {}
                        // Immediate popup toast
                        com.avinashpatil.app.automessage.utils.UiFeedback.showNeumorphicToast(
                            this@CallDetectionService,
                            "Auto message sent successfully ✅\nFrom Auto Messaging Services"
                        )
                        // High-priority notification via app channel
                        try {
                            val soundEnabled = try { dataStoreRepository.isNotificationSoundEnabled().first() } catch (_: Exception) { true }
                            val builder = NotificationCompat.Builder(this@CallDetectionService, com.avinashpatil.app.automessage.AutoMessageApplication.CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logo))
                                .setContentTitle("Auto Messaging")
                                .setContentText("Auto message sent successfully ✅\nTo: $phone")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setAutoCancel(true)
                                .setSilent(!soundEnabled)
                                .apply {
                                    if (soundEnabled) setDefaults(NotificationCompat.DEFAULT_ALL) else setDefaults(0)
                                }
                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                val nm = getSystemService(NotificationManager::class.java)
                                nm.notify(2001, builder.build())
                            }
                        } catch (_: Exception) { }
                    }
                    android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        autoReplyRepository.markLogFailed(id = logId, attempts = attempts, error = "Generic failure")
                        try {
                            val log = autoReplyRepository.getAutoReplyLogById(logId)
                            if (log != null) openSmsAppFallback(phone, log.messageText ?: "")
                        } catch (_: Exception) {}
                    }
                    android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        autoReplyRepository.markLogFailed(id = logId, attempts = attempts, error = "No service")
                    }
                    android.telephony.SmsManager.RESULT_ERROR_NULL_PDU -> {
                        autoReplyRepository.markLogFailed(id = logId, attempts = attempts, error = "Null PDU")
                        try {
                            val log = autoReplyRepository.getAutoReplyLogById(logId)
                            if (log != null) openSmsAppFallback(phone, log.messageText ?: "")
                        } catch (_: Exception) {}
                    }
                    android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        autoReplyRepository.markLogFailed(id = logId, attempts = attempts, error = "Radio off")
                    }
                    else -> {
                        autoReplyRepository.markLogFailed(id = logId, attempts = attempts, error = "Unknown error: $result")
                    }
                }
            }
        }
    }

    private val smsDeliveredReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: android.content.Context?, intent: Intent?) {
            val logId = intent?.getLongExtra("log_id", -1L) ?: -1L
            val phone = intent?.getStringExtra("phone") ?: ""
            val result = resultCode
            serviceScope.launch {
                when (result) {
                    android.app.Activity.RESULT_OK -> {
                        autoReplyRepository.markLogDelivered(id = logId, deliveredTs = System.currentTimeMillis())
                        // Delivery success handled; we already showed popup on SENT
                        // Also mark as sent today to enforce once-per-day rule even on delivery callback
                        try { com.avinashpatil.app.automessage.utils.DailyMessageTracker.markSentToday(this@CallDetectionService, phone) } catch (_: Exception) {}
                        // System notification on delivery
                        try {
                            val soundEnabled = try { dataStoreRepository.isNotificationSoundEnabled().first() } catch (_: Exception) { true }
                            val builder = NotificationCompat.Builder(this@CallDetectionService, com.avinashpatil.app.automessage.AutoMessageApplication.CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logo))
                                .setContentTitle("Auto Messaging")
                                .setContentText("Message delivered ✅\nTo: $phone")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setAutoCancel(true)
                                .setSilent(!soundEnabled)
                                .apply {
                                    if (soundEnabled) setDefaults(NotificationCompat.DEFAULT_ALL) else setDefaults(0)
                                }
                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                val nm = getSystemService(NotificationManager::class.java)
                                nm.notify(2002, builder.build())
                            }
                        } catch (_: Exception) { }
                    }
                    android.app.Activity.RESULT_CANCELED -> {
                        // Delivery failed or canceled; keep status as SENT, optionally record error
                        autoReplyRepository.markLogFailed(id = logId, attempts = 1, error = "Delivery canceled")
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        // Auto-enable auto-reply on first run
        serviceScope.launch {
            try {
                val first = dataStoreRepository.isFirstTimeUser().first()
                if (first) {
                    dataStoreRepository.saveAutoReplyEnabled(true)
                    dataStoreRepository.saveFirstTimeUser(false)
                    android.util.Log.d(TAG, "First run: auto-reply enabled by default")
                }
            } catch (_: Exception) { }
        }

        createNotificationChannel()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Explicitly mark this as a dataSync foreground service to satisfy Android 14+ policy
                startForeground(
                    NOTIFICATION_ID,
                    createNotification(),
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(NOTIFICATION_ID, createNotification())
            }
        } catch (se: SecurityException) {
            // If type is rejected by policy, avoid crash and let WorkManager fallback keep things alive
            android.util.Log.e(TAG, "startForeground rejected by policy", se)
            stopForeground(true)
        }
        // Register receivers for SMS status
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsSentReceiver, IntentFilter(ACTION_SMS_SENT), Context.RECEIVER_NOT_EXPORTED)
            registerReceiver(smsDeliveredReceiver, IntentFilter(ACTION_SMS_DELIVERED), Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(smsSentReceiver, IntentFilter(ACTION_SMS_SENT))
            registerReceiver(smsDeliveredReceiver, IntentFilter(ACTION_SMS_DELIVERED))
        }

        // Also schedule daily reset work to enforce once-per-day sending
        try { scheduleDailyResetWork() } catch (_: Exception) {}

        // Register ContentObserver for call log to detect answered calls in near real-time
        try {
            callLogObserver = object : android.database.ContentObserver(android.os.Handler(android.os.Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean, uri: android.net.Uri?) {
                    super.onChange(selfChange, uri)
                    serviceScope.launch {
                        try {
                            val latest = getLatestAnsweredCallFromLog()
                            if (latest != null) {
                                android.util.Log.d(TAG, "ContentObserver detected answered call id=${latest.id} number=${latest.number}")
                                checkAndSendAutoReply(latest.number)
                            }
                        } catch (e: Exception) {
                            android.util.Log.e(TAG, "Observer error", e)
                        }
                    }
                }
            }
            contentResolver.registerContentObserver(android.provider.CallLog.Calls.CONTENT_URI, true, callLogObserver!!)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to register call log observer", e)
        }

        try {
            tm = getSystemService(TelephonyManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val executor = ContextCompat.getMainExecutor(this)
                val callback = object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                    override fun onCallStateChanged(state: Int) {
                        when (state) {
                            TelephonyManager.CALL_STATE_RINGING -> handleCallStateChanged(TelephonyManager.EXTRA_STATE_RINGING, null)
                            TelephonyManager.CALL_STATE_OFFHOOK -> handleCallStateChanged(TelephonyManager.EXTRA_STATE_OFFHOOK, null)
                            TelephonyManager.CALL_STATE_IDLE -> handleCallStateChanged(TelephonyManager.EXTRA_STATE_IDLE, null)
                        }
                    }
                }
                callStateCallback = callback
                tm.registerTelephonyCallback(executor, callback)
            } else {
                callStateListener = object : PhoneStateListener() {
                    override fun onCallStateChanged(state: Int, incomingNumber: String?) {
                        when (state) {
                            TelephonyManager.CALL_STATE_RINGING -> handleCallStateChanged(TelephonyManager.EXTRA_STATE_RINGING, incomingNumber)
                            TelephonyManager.CALL_STATE_OFFHOOK -> handleCallStateChanged(TelephonyManager.EXTRA_STATE_OFFHOOK, incomingNumber)
                            TelephonyManager.CALL_STATE_IDLE -> handleCallStateChanged(TelephonyManager.EXTRA_STATE_IDLE, incomingNumber)
                        }
                    }
                }
                tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to register PhoneStateListener", e)
        }

        // Schedule keepalive to ensure restart if killed
        scheduleKeepAlive()

        // Start 1-minute call log polling loop to ensure 100% background detection
        serviceScope.launch {
            while (true) {
                try {
                    withWakeLock {
                        val latest = getLatestAnsweredCallFromLog()
                        if (latest != null) {
                            val lastSeen = autoReplyRepository.getLastSeenCall()
                            if (lastSeen?.callId != latest.id.toString()) {
                                android.util.Log.d(TAG, "Poller detected new answered call id=${latest.id} number=${latest.number}")
                                checkAndSendAutoReply(latest.number)
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Poller error", e)
                }
                delay(60_000)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "CALL_STATE_CHANGED" -> {
                val state = intent.getStringExtra("state")
                val phoneNumber = intent.getStringExtra("phone_number")
                handleCallStateChanged(state, phoneNumber)
            }
            "NEW_OUTGOING_CALL" -> {
                val phoneNumber = intent.getStringExtra("phone_number")
                handleOutgoingCall(phoneNumber)
            }
            "MANUAL_SEND" -> {
                val phoneNumber = intent.getStringExtra("phone_number")
                serviceScope.launch {
                    if (!phoneNumber.isNullOrBlank()) {
                        try {
                            checkAndSendAutoReply(phoneNumber)
                        } catch (e: Exception) { android.util.Log.e(TAG, "Manual send failed", e) }
                    }
                }
            }
            ACTION_KEEPALIVE -> {
                // No-op; ensures service stays sticky
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        // Unregister receivers
        try { unregisterReceiver(smsSentReceiver) } catch (_: Exception) {}
        try { unregisterReceiver(smsDeliveredReceiver) } catch (_: Exception) {}
        // Unregister ContentObserver
        try {
            callLogObserver?.let { contentResolver.unregisterContentObserver(it) }
            callLogObserver = null
        } catch (_: Exception) {}
        try {
            if (this::tm.isInitialized) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val cb = callStateCallback
                    if (cb != null) {
                        tm.unregisterTelephonyCallback(cb)
                        callStateCallback = null
                    }
                } else {
                    if (callStateListener != null) {
                        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
                        callStateListener = null
                    }
                }
            }
        } catch (_: Exception) {}
        // Reschedule keepalive
        scheduleKeepAlive()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        try {
            val intent = Intent(this, CallDetectionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (_: Exception) { }
        // Reschedule keepalive when task removed
        scheduleKeepAlive()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Call Detection Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors incoming calls for auto-reply functionality"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Auto Message Service")
            .setContentText("Monitoring calls for auto-reply")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
        // Add battery optimization whitelist action if applicable
        batteryOptimizationPendingIntent()?.let { pending ->
            builder.addAction(0, "Allow Always-On", pending)
        }
        // Add OEM autostart/background settings action (removed unresolved helper)
        // If needed, this can be reintroduced via a dedicated util with manufacturer-specific intents.
        // Add quick action to app settings to grant permissions
        try {
            val permIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:$packageName")
            }
            val pi = PendingIntent.getActivity(this, 1, permIntent, pendingFlags())
            builder.addAction(0, "Grant Permissions", pi)
        } catch (_: Exception) { }
        return builder.build()
    }

    private fun batteryOptimizationPendingIntent(): PendingIntent? {
        return try {
            val pm = getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            val pkg = packageName
            val isIgnoring = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) pm.isIgnoringBatteryOptimizations(pkg) else true
            if (!isIgnoring && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = android.net.Uri.parse("package:$pkg")
                }
                PendingIntent.getActivity(this, 0, intent, pendingFlags())
            } else null
        } catch (_: Exception) { null }
    }

    private suspend inline fun <T> withWakeLock(timeoutMs: Long = 30_000, crossinline block: suspend () -> T): T {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AutoMessage:CallProcessing")
        try {
            wl.acquire(timeoutMs)
        } catch (_: Exception) {}
        try {
            return block()
        } finally {
            try { if (wl.isHeld) wl.release() } catch (_: Exception) {}
        }
    }

    private fun scheduleKeepAlive(intervalMs: Long = 20 * 60_000) {
        try {
            val am = getSystemService(AlarmManager::class.java)
            val intent = Intent(this, KeepAliveReceiver::class.java).apply { action = ACTION_KEEPALIVE }
            val pi = PendingIntent.getBroadcast(this, 0, intent, pendingFlags())
            val triggerAt = System.currentTimeMillis() + intervalMs
            val canExact = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) am?.canScheduleExactAlarms() == true else true
            } catch (_: Throwable) { false }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canExact) {
                    am?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                } else {
                    am?.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                }
            } else {
                am?.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } catch (_: Exception) { }
    }

    private fun handleCallStateChanged(state: String?, phoneNumber: String?) {
        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                // Incoming call ringing - no action needed for successful call detection
                android.util.Log.d(TAG, "Incoming call ringing: $phoneNumber")
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                // Call answered: mark number as answered for successful call tracking
                serviceScope.launch {
                    val number = phoneNumber ?: getLatestNumberFromLog()
                    if (!number.isNullOrBlank()) {
                        val now = System.currentTimeMillis()
                        answeredNumbers[number] = now
                        val isOutgoing = lastOutgoingDialed[number]?.let { now - it < 5 * 60_000 } ?: false
                        callTypeByNumber[number] = if (isOutgoing) "OUTGOING_ANSWERED" else "INCOMING_ANSWERED"
                        android.util.Log.d(TAG, "Call answered: $number, type: ${callTypeByNumber[number]}")
                    }
                }
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                // Call ended: send auto-reply only if it was a successful call (answered)
                serviceScope.launch {
                    withWakeLock {
                        val number = phoneNumber ?: getLatestNumberFromLog()
                        if (!number.isNullOrBlank()) {
                            val wasAnswered = answeredNumbers.containsKey(number)
                            val callTypeStr = callTypeByNumber[number] ?: "ANSWERED_CALL"
                            // Cleanup state
                            answeredNumbers.remove(number)
                            callTypeByNumber.remove(number)
                            lastOutgoingDialed.remove(number)
                            android.util.Log.d(TAG, "Call ended: $number, wasAnswered: $wasAnswered")
                            if (wasAnswered && !hasSentRecently(number)) {
                                // Rely on call log to confirm answered duration and call type; respects delay setting
                                android.util.Log.d(TAG, "Checking call log before sending auto-reply: $number")
                                checkAndSendAutoReply(number)
                            } else if (!wasAnswered) {
                                android.util.Log.d(TAG, "Call not answered, skipping auto-reply: $number")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleOutgoingCall(phoneNumber: String?) {
        serviceScope.launch {
            val number = phoneNumber ?: getLatestNumberFromLog()
            if (!number.isNullOrBlank()) {
                // Record dialed number; OFFHOOK will mark answered for successful outgoing calls
                lastOutgoingDialed[number] = System.currentTimeMillis()
                android.util.Log.d(TAG, "Outgoing call started: $number")
            }
        }
    }

    private suspend fun scheduleQuickReply(phoneNumber: String, callType: String) {
        try {
            val isEnabled = dataStoreRepository.isAutoReplyEnabled().first()
            if (!isEnabled) return
            if (hasSentRecently(phoneNumber)) return
            if (com.avinashpatil.app.automessage.utils.DailyMessageTracker.hasSentToday(this@CallDetectionService, phoneNumber)) {
                android.util.Log.d(TAG, "Already sent today to $phoneNumber, skipping quick reply")
                return
            }

            val contact = com.avinashpatil.app.automessage.utils.ContactHelper.getContactByPhoneNumber(this@CallDetectionService, phoneNumber)

            if (contact?.id != null) {
                val savedContact = contactRepository.getContactById(contact.id)
                if (savedContact?.isBlacklisted == true) return
            }

            val message = getMessageForContact(contact)
            // Small delay to let systems stabilize
            delay(1500L)
            sendAutoReply(phoneNumber, message, contact, System.currentTimeMillis().toString(), callType)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error in scheduleQuickReply", e)
        }
    }

    private suspend fun checkAndSendAutoReply(phoneNumber: String) {
        try {
            val isEnabled = dataStoreRepository.isAutoReplyEnabled().first()
            if (!isEnabled) { android.util.Log.d(TAG, "Auto-reply disabled, skipping for $phoneNumber"); return }
            if (hasSentRecently(phoneNumber)) { android.util.Log.d(TAG, "Recently sent to $phoneNumber, skipping"); return }
            if (com.avinashpatil.app.automessage.utils.DailyMessageTracker.hasSentToday(this@CallDetectionService, phoneNumber)) {
                android.util.Log.d(TAG, "Already sent today to $phoneNumber, skipping")
                return
            }

            val callLogEntry = getLatestCallLog(phoneNumber)
            // Only send for answered calls (incoming/outgoing) with duration > 0
            val isAnswered = callLogEntry != null && (
                callLogEntry.type == android.provider.CallLog.Calls.INCOMING_TYPE ||
                callLogEntry.type == android.provider.CallLog.Calls.OUTGOING_TYPE
            ) && callLogEntry.durationSec > 0
            if (!isAnswered) { android.util.Log.d(TAG, "Not answered or duration=0, skipping for $phoneNumber"); return }

            val lastSeenCall = autoReplyRepository.getLastSeenCall()
            if (lastSeenCall?.callId == callLogEntry?.id?.toString()) { android.util.Log.d(TAG, "Call already processed, skipping for $phoneNumber"); return }

            val contact = com.avinashpatil.app.automessage.utils.ContactHelper.getContactByPhoneNumber(this@CallDetectionService, phoneNumber)

            if (contact?.id != null) {
                val savedContact = contactRepository.getContactById(contact.id)
                if (savedContact?.isBlacklisted == true) { android.util.Log.d(TAG, "Contact blacklisted, skipping for $phoneNumber"); return }
            }

            val message = getMessageForContact(contact)
            val callTypeStr = when (callLogEntry?.type) {
                android.provider.CallLog.Calls.INCOMING_TYPE -> "INCOMING_ANSWERED"
                android.provider.CallLog.Calls.OUTGOING_TYPE -> "OUTGOING_ANSWERED"
                else -> "ANSWERED_CALL"
            }

            val callIdStr = callLogEntry?.id?.toString() ?: System.currentTimeMillis().toString()
            val canProcess = com.avinashpatil.app.automessage.utils.DuplicatePreventer.shouldProcess(
                this@CallDetectionService,
                callId = callIdStr,
                phoneNumber = phoneNumber,
                windowMs = 30_000
            )
            if (!canProcess) { android.util.Log.d(TAG, "Duplicate preventer blocked for $phoneNumber"); return }

            // Send immediately (remove delay)
            sendAutoReply(phoneNumber, message, contact, callIdStr, callTypeStr)
            com.avinashpatil.app.automessage.utils.DuplicatePreventer.markProcessed(this@CallDetectionService, callIdStr, phoneNumber)

            // Optionally blast to group members if enabled
            try {
                val groupAutoEnabled = dataStoreRepository.isGroupAutoReplyEnabled().first()
                val groupId = contact?.groupId
                if (groupAutoEnabled && groupId != null) {
                    sendGroupAutoReplies(groupId, message, contact, callIdStr, callTypeStr)
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Group auto-reply dispatch failed", e)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Send auto-replies to all members of the contact's group except blacklisted and original contact
    private suspend fun sendGroupAutoReplies(
        groupId: Long,
        message: String,
        triggeringContact: com.avinashpatil.app.automessage.data.entity.ContactEntity?,
        callId: String,
        callType: String
    ) {
        try {
            // Fetch current group members snapshot
            val membersFlow = contactRepository.getContactsByGroup(groupId)
            val members = membersFlow.first()
            if (members.isEmpty()) return

            android.util.Log.d(TAG, "Dispatching group auto-replies to ${members.size} members for groupId=$groupId")

            for (member in members) {
                val phone = member.phoneNumber?.trim().orEmpty()
                if (phone.isEmpty()) continue
                // Skip the triggering contact and blacklisted contacts and recent sends
                if (triggeringContact?.id == member.id) continue
                if (member.isBlacklisted) continue
                if (hasSentRecently(phone)) continue
                if (com.avinashpatil.app.automessage.utils.DailyMessageTracker.hasSentToday(this@CallDetectionService, phone)) continue

                try {
                    sendAutoReply(phone, message, member, callId, "GROUP_" + callType)
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Failed group send to ${member.phoneNumber}", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error during group auto-replies", e)
        }
    }

    private suspend fun getMessageForContact(contact: ContactEntity?): String {
        return when {
            contact?.groupId != null -> {
                val group = groupRepository.getGroupById(contact.groupId!!)
                val customMessage = messageRepository.getMessageByGroupType(group?.name ?: "DEFAULT")
                customMessage?.body ?: "Testing purpose."
            }
            contact?.isPriority == true -> {
                val customMessage = messageRepository.getMessageByGroupType("VIP")
                customMessage?.body ?: "Testing purpose."
            }
            else -> {
                val defaultMessage = messageRepository.getDefaultMessage()
                defaultMessage?.body ?: "Testing purpose."
            }
        }
    }

    private fun pendingFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
    }

    private fun hasAllMessagingPermissions(): Boolean {
        // Only require core permissions. READ/RECEIVE_SMS are restricted on Android 15
        val required = arrayOf(
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.SEND_SMS
        )
        return required.all { p ->
            ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun promptGrantPermissions() {
        try {
            // Toast + settings shortcut through notification already exists
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                try {
                    android.widget.Toast.makeText(this, "Permissions required for auto messaging.", android.widget.Toast.LENGTH_SHORT).show()
                } catch (_: Exception) {}
            }
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:" + packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (_: Exception) { }
    }

    private suspend fun sendAutoReply(phoneNumber: String, message: String, contact: ContactEntity?, callId: String, callType: String) {
        var logId: Long = -1L
        try {
            android.util.Log.d(TAG, "Starting sendAutoReply for: $phoneNumber, callType: $callType")
            // Guard against missing runtime permissions in background service
            if (!hasAllMessagingPermissions()) {
                android.util.Log.e(TAG, "SMS permissions not granted")
                promptGrantPermissions()
                return
            }
            
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getSystemService(SmsManager::class.java) ?: SmsManager.getDefault()
            } else {
                SmsManager.getDefault()
            }
        
            val ts = System.currentTimeMillis()
            val dayKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(ts))
        
            // Pre-log with PENDING status and unique phone+dayKey
            val preLog = AutoReplyLogEntity(
                contactId = contact?.id ?: phoneNumber,
                contactName = contact?.name ?: phoneNumber,
                phoneNumber = phoneNumber,
                messageText = message,
                timestamp = ts,
                dayKey = dayKey,
                callType = callType,
                isAutoReply = true,
                status = "PENDING",
                attempts = 0,
                error = null,
                sentTimestamp = null,
                deliveredTimestamp = null
            )
        
            var nowAttempt = 1
            try {
                logId = autoReplyRepository.logAutoReplyReturnId(preLog)
                android.util.Log.d(TAG, "Created auto-reply log with ID: $logId")
            } catch (e: Exception) {
                if (e is android.database.sqlite.SQLiteConstraintException) {
                    // Duplicate for phone+dayKey detected; decide whether to resend
                    val existing = autoReplyRepository.getLogByPhoneAndDay(phoneNumber, dayKey)
                    if (existing == null) {
                        android.util.Log.w(TAG, "Unique conflict but no existing record found; skipping for $phoneNumber")
                        return
                    }
                    when (existing.status) {
                        "DELIVERED", "SENT", "PENDING" -> {
                            android.util.Log.d(TAG, "Already logged for today with status=${existing.status}; skipping send for $phoneNumber")
                            return
                        }
                        "FAILED" -> {
                            // Simple exponential backoff: skip if too soon or too many attempts
                            val attempts = existing.attempts
                            val lastSentTs = existing.sentTimestamp ?: 0L
                            val backoffMs = (15 * 60_000L) * (1L shl attempts.coerceAtLeast(0))
                            val elapsed = ts - lastSentTs
                            if (attempts >= 3) {
                                android.util.Log.d(TAG, "Max retry attempts reached; skipping for $phoneNumber")
                                return
                            }
                            if (lastSentTs > 0 && elapsed < backoffMs) {
                                android.util.Log.d(TAG, "Backoff active (${elapsed}ms < ${backoffMs}ms); skipping for $phoneNumber")
                                return
                            }
                            logId = existing.id
                            nowAttempt = attempts + 1
                            android.util.Log.d(TAG, "Retrying failed send; logId=$logId attempt=$nowAttempt")
                        }
                        else -> {
                            android.util.Log.d(TAG, "Unknown status=${existing.status}; skipping")
                            return
                        }
                    }
                } else {
                    throw e
                }
            }
        
            val parts: ArrayList<String> = smsManager.divideMessage(message)
            val sentIntents = ArrayList<PendingIntent>()
            val deliverIntents = ArrayList<PendingIntent>()
        
            if (parts.size > 1) {
                android.util.Log.d(TAG, "Sending multipart SMS with ${parts.size} parts")
                for (i in parts.indices) {
                    val sentIntent = Intent(ACTION_SMS_SENT).apply {
                        putExtra("log_id", logId)
                        putExtra("attempts", nowAttempt)
                        putExtra("phone", phoneNumber)
                    }
                    val deliveredIntent = Intent(ACTION_SMS_DELIVERED).apply {
                        putExtra("log_id", logId)
                        putExtra("phone", phoneNumber)
                    }
                    sentIntents.add(PendingIntent.getBroadcast(this, i, sentIntent, pendingFlags()))
                    deliverIntents.add(PendingIntent.getBroadcast(this, i, deliveredIntent, pendingFlags()))
                }
                // Ensure CPU stays awake during send
                withWakeLock {
                    smsManager.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliverIntents)
                }
            } else {
                android.util.Log.d(TAG, "Sending single SMS")
                val sentIntent = Intent(ACTION_SMS_SENT).apply {
                    putExtra("log_id", logId)
                    putExtra("attempts", nowAttempt)
                    putExtra("phone", phoneNumber)
                }
                val deliveredIntent = Intent(ACTION_SMS_DELIVERED).apply {
                    putExtra("log_id", logId)
                    putExtra("phone", phoneNumber)
                }
                val piSent = PendingIntent.getBroadcast(this, 0, sentIntent, pendingFlags())
                val piDelivered = PendingIntent.getBroadcast(this, 0, deliveredIntent, pendingFlags())
                // Ensure CPU stays awake during send
                withWakeLock {
                    smsManager.sendTextMessage(phoneNumber, null, message, piSent, piDelivered)
                }
            }
        
            // Mark last seen call after initiating send
            autoReplyRepository.updateLastSeenCall(callId, contact?.id)
            recentlySent[phoneNumber] = System.currentTimeMillis()
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to send auto-reply to $phoneNumber", e)
            // If we created a log row, mark as FAILED
            if (logId > 0) {
                try {
                    autoReplyRepository.markLogFailed(id = logId, attempts = 1, error = e.message)
                } catch (inner: Exception) {
                    android.util.Log.e(TAG, "Failed to mark log failed", inner)
                }
            }
            // Guided fallback: suggest setting app as default SMS if background blocked
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    val rm = getSystemService(android.app.role.RoleManager::class.java)
                    if (rm.isRoleAvailable(android.app.role.RoleManager.ROLE_SMS) && !rm.isRoleHeld(android.app.role.RoleManager.ROLE_SMS)) {
                        val intent = rm.createRequestRoleIntent(android.app.role.RoleManager.ROLE_SMS)
                        val pi = PendingIntent.getActivity(this, 42, intent, pendingFlags())
                        val builder = androidx.core.app.NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                            .setContentTitle("Allow AutoMessaging to send SMS")
                            .setContentText("Tap to set AutoMessaging as default SMS app for reliable delivery.")
                            .setSmallIcon(com.avinashpatil.app.automessage.R.drawable.ic_notification)
                            .setAutoCancel(true)
                            .addAction(0, "Set Default SMS", pi)
                        val nm = getSystemService(android.app.NotificationManager::class.java)
                        nm.notify(1002, builder.build())
                    }
                }
            } catch (_: Exception) { }
            // Fallback: open default SMS app prefilled
            try {
                openSmsAppFallback(phoneNumber, message)
            } catch (_: Exception) { }
        }
    }

    private suspend fun getLatestCallLog(phoneNumber: String): CallLogEntry? {
        return try {
            val uri = android.provider.CallLog.Calls.CONTENT_URI
            val projection = arrayOf(
                android.provider.CallLog.Calls._ID,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.DURATION
            )
            val sortOrder = android.provider.CallLog.Calls.DATE + " DESC"
            val cursor = contentResolver.query(uri, projection, null, null, sortOrder)
            var entry: CallLogEntry? = null
            cursor?.use { c ->
                var checked = 0
                while (c.moveToNext() && checked < 50) {
                    checked++
                    val idIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls._ID)
                    val numIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER)
                    val typeIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.TYPE)
                    val durIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.DURATION)
                    val num = c.getString(numIdx)
                    if (!numbersMatch(num, phoneNumber)) continue
                    entry = CallLogEntry(
                        id = c.getLong(idIdx),
                        type = c.getInt(typeIdx),
                        durationSec = c.getInt(durIdx)
                    )
                    break
                }
            }
            val desc = if (entry != null) {
                "id=${entry.id}, type=${entry.type}, duration=${entry.durationSec}"
            } else {
                "none"
            }
            android.util.Log.d(TAG, "Latest call log for $phoneNumber: $desc")
            entry
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getLatestNumberFromLog(): String? {
        return try {
            val uri = android.provider.CallLog.Calls.CONTENT_URI
            val projection = arrayOf(
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.DATE
            )
            val sortOrder = android.provider.CallLog.Calls.DATE + " DESC"
            val cursor = contentResolver.query(uri, projection, null, null, sortOrder)
            var number: String? = null
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val numberIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER)
                    number = c.getString(numberIdx)
                }
            }
            number
        } catch (e: Exception) {
            null
        }
    }

    private data class CallLogEntry(val id: Long, val type: Int, val durationSec: Int)
    private data class CallLogEntryAny(val id: Long, val number: String, val type: Int, val durationSec: Int)

    private suspend fun getLatestAnsweredCallFromLog(): CallLogEntryAny? {
        return try {
            val uri = android.provider.CallLog.Calls.CONTENT_URI
            val projection = arrayOf(
                android.provider.CallLog.Calls._ID,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.DURATION
            )
            val sortOrder = android.provider.CallLog.Calls.DATE + " DESC"
            val cursor = contentResolver.query(uri, projection, null, null, sortOrder)
            var entry: CallLogEntryAny? = null
            cursor?.use { c ->
                var checked = 0
                while (c.moveToNext() && checked < 50) {
                    checked++
                    val idIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls._ID)
                    val numIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER)
                    val typeIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.TYPE)
                    val durIdx = c.getColumnIndexOrThrow(android.provider.CallLog.Calls.DURATION)
                    val type = c.getInt(typeIdx)
                    val dur = c.getInt(durIdx)
                    if ((type == android.provider.CallLog.Calls.INCOMING_TYPE || type == android.provider.CallLog.Calls.OUTGOING_TYPE) && dur > 0) {
                        entry = CallLogEntryAny(
                            id = c.getLong(idIdx),
                            number = c.getString(numIdx),
                            type = type,
                            durationSec = dur
                        )
                        break
                    }
                }
            }
            entry
        } catch (e: Exception) {
            null
        }
    }

    private fun scheduleDailyResetWork() {
        // Compute initial delay to next local midnight, then repeat every 24h
        val cal = java.util.Calendar.getInstance()
        val now = cal.timeInMillis
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        var nextMidnight = cal.timeInMillis
        if (nextMidnight <= now) {
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            nextMidnight = cal.timeInMillis
        }
        val initialDelayMs = nextMidnight - now

        val constraints = Constraints.Builder().build()
        val request = PeriodicWorkRequestBuilder<DailyResetWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag("DailyResetWork")
            .build()

        WorkManager.getInstance(this@CallDetectionService).enqueueUniquePeriodicWork(
            "DailyResetWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
