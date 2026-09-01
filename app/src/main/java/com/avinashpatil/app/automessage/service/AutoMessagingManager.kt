package com.avinashpatil.app.automessage.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.avinashpatil.app.automessage.data.repository.DataStoreRepository
import com.avinashpatil.app.automessage.receiver.KeepAliveReceiver
import com.avinashpatil.app.automessage.utils.AutoMessagingStateChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoMessagingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreRepository: DataStoreRepository
) {
    companion object {
        private const val TAG = "AutoMessagingManager"
        private const val ACTION_KEEPALIVE = "com.avinashpatil.app.automessage.ACTION_KEEPALIVE"
        private const val NOTIFICATION_ID = 1
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var cachedAutoMessagingEnabled: Boolean = true

    init {
        scope.launch {
            try {
                dataStoreRepository.isAutoMessagingEnabled().collect { enabled ->
                    cachedAutoMessagingEnabled = enabled
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error collecting auto messaging state", e)
            }
        }
    }

    fun isAutoMessagingEnabled(): Boolean {
        return cachedAutoMessagingEnabled
    }

    suspend fun setAutoMessagingEnabled(enabled: Boolean) {
        Log.d(TAG, "AUTO_MSG: ${if (enabled) "enabled" else "disabled"}")
        dataStoreRepository.saveAutoMessagingEnabled(enabled)
        cachedAutoMessagingEnabled = enabled
        AutoMessagingStateChecker.syncEnabled(context)

        if (enabled) {
            startAutomation()
        } else {
            stopAutomation()
        }
    }

    private fun startAutomation() {
        Log.d(TAG, "AUTO_MSG: service started")
        startCallDetectionService()
        scheduleKeepAlive()
        scheduleCallLogPoller()
    }

    private fun stopAutomation() {
        Log.d(TAG, "AUTO_MSG: service stopped")
        stopCallDetectionService()
        cancelKeepAlive()
        cancelCallLogPoller()
        cancelAllWorkers()
        removeNotification()
    }

    private fun startCallDetectionService() {
        try {
            val serviceIntent = Intent(context, CallDetectionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start CallDetectionService", e)
        }
    }

    private fun stopCallDetectionService() {
        try {
            val serviceIntent = Intent(context, CallDetectionService::class.java)
            context.stopService(serviceIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop CallDetectionService", e)
        }
    }

    fun scheduleKeepAlive(intervalMs: Long = 20 * 60_000) {
        try {
            val am = context.getSystemService(AlarmManager::class.java)
            val intent = Intent(context, KeepAliveReceiver::class.java).apply { action = ACTION_KEEPALIVE }
            val pi = PendingIntent.getBroadcast(context, 0, intent, pendingFlags())
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

    fun cancelKeepAlive() {
        try {
            val am = context.getSystemService(AlarmManager::class.java)
            val intent = Intent(context, KeepAliveReceiver::class.java).apply { action = ACTION_KEEPALIVE }
            val pi = PendingIntent.getBroadcast(context, 0, intent, pendingFlags())
            am?.cancel(pi)
        } catch (_: Exception) { }
    }

    private fun scheduleCallLogPoller() {
        try {
            val constraints = Constraints.Builder().build()
            val request = PeriodicWorkRequestBuilder<CallLogPollerWorker>(15, java.util.concurrent.TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag("CallLogPollerWork")
                .build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "CallLogPollerWork",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        } catch (_: Exception) { }
    }

    fun cancelCallLogPoller() {
        try {
            WorkManager.getInstance(context).cancelUniqueWork("CallLogPollerWork")
        } catch (_: Exception) { }
    }

    private fun cancelAllWorkers() {
        try {
            WorkManager.getInstance(context).cancelUniqueWork("DailyResetWork")
        } catch (_: Exception) { }
    }

    private fun removeNotification() {
        try {
            val nm = context.getSystemService(NotificationManager::class.java)
            nm?.cancel(NOTIFICATION_ID)
        } catch (_: Exception) { }
    }

    private fun pendingFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
    }

    fun destroy() {
        scope.cancel()
    }
}
