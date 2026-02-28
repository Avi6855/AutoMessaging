package com.avinashpatil.app.automessage.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.avinashpatil.app.automessage.R
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepository
import com.avinashpatil.app.automessage.data.repository.DiscrepancyRepository
import com.avinashpatil.app.automessage.data.entity.DiscrepancyLogEntity
import com.avinashpatil.app.automessage.utils.CallLogHelper

@AndroidEntryPoint
class CallVerificationService : Service() {
    @Inject lateinit var autoReplyRepository: AutoReplyRepository
    @Inject lateinit var discrepancyRepository: DiscrepancyRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val CHANNEL_ID = "call_verification_channel"
        private const val NOTIF_ID = 2
        private const val TAG = "CallVerificationService"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIF_ID, createNotification())
        scope.launch { verificationLoop() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "TRIGGER_VERIFICATION") {
            scope.launch { runVerificationOnce() }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Call Verification",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Verifies answered calls for auto-reply coverage"
                setShowBadge(false)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Verification Service")
            .setContentText("Ensuring answered calls get auto-replies")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private suspend fun verificationLoop() {
        while (true) {
            try {
                runVerificationOnce()
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Verification loop error", e)
            }
            delay(15 * 60_000) // every 15 minutes
        }
    }

    private suspend fun runVerificationOnce() {
        val latestTs = CallLogHelper.getLatestCallTimestamp(this)
        val since = latestTs - 24 * 60 * 60_000 // last 24 hours window
        val answered = CallLogHelper.getAnsweredCallsSince(this, since)
        for (entry in answered) {
            try {
                val phone = entry.number
                val startWindow = entry.date - 10 * 60_000 // 10 minutes before
                val endWindow = entry.date + 10 * 60_000  // 10 minutes after
                val successCount = autoReplyRepository.getSuccessfulAutoRepliesByPhoneInRange(
                    phone = phone,
                    startTime = startWindow,
                    endTime = endWindow
                )
                if (successCount <= 0) {
                    // Log discrepancy for review and possible manual send
                    val status = "UNRESOLVED"
                    val callTypeStr = when (entry.type) {
                        android.provider.CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                        android.provider.CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                        else -> "UNKNOWN"
                    }
                    val log = DiscrepancyLogEntity(
                        callId = entry.id.toString(),
                        phoneNumber = phone,
                        contactId = null,
                        contactName = "",
                        callType = callTypeStr,
                        callTimestamp = entry.date,
                        durationSec = entry.durationSec,
                        status = status,
                        notes = "Answered call missing auto-reply within window"
                    )
                    discrepancyRepository.insert(log)
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Verification error for ${entry.number}", e)
            }
        }
    }
}