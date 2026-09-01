package com.avinashpatil.app.automessage

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.avinashpatil.app.automessage.service.CallLogPollerWorker
import com.avinashpatil.app.automessage.utils.AutoMessagingStateChecker
import com.avinashpatil.app.automessage.utils.DailyHistoryClearScheduler
import com.avinashpatil.app.automessage.workers.AutoReplyHistoryClearWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class AutoMessageApplication : Application() {

    companion object {
        const val CHANNEL_ID = "auto_message_channel"
        const val CHANNEL_NAME = "Auto Message"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        AutoMessagingStateChecker.init(this)
        scheduleAutoReplyHistoryClear()

        val enabled = AutoMessagingStateChecker.isAutoMessagingEnabled(this)
        Log.d("AutoMessageApplication", "AUTO_MSG: app started, autoMessaging=$enabled")

        if (enabled) {
            startCallDetectionService()
            scheduleCallLogPoller()
        } else {
            Log.d("AutoMessageApplication", "AUTO_MSG: poller not scheduled because automation disabled")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Auto Message Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification channel for Auto Message background service"
                setShowBadge(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleAutoReplyHistoryClear() {
        // Primary: exact alarm at 00:01 IST (forceful, fallback WorkManager kept for safety)
        DailyHistoryClearScheduler.scheduleDailyClear(this)
        // Keep WorkManager as inexact fallback (runs ~00:02 IST if exact alarm denied)
        AutoReplyHistoryClearWorker.scheduleDailyClear(this)
    }

    private fun startCallDetectionService() {
        try {
            // Avoid FGS start when app is in background (e.g., after BOOT_COMPLETED) — will throw ForegroundServiceStartNotAllowedException on Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isAppInForeground()) {
                Log.w("AutoMessageApplication", "AUTO_MSG: deferring service start (background), scheduling poller instead")
                scheduleCallLogPoller()
                return
            }
            val serviceIntent = android.content.Intent(this, com.avinashpatil.app.automessage.service.CallDetectionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            Log.d("AutoMessageApplication", "AUTO_MSG: CallDetectionService started")
        } catch (e: SecurityException) {
            Log.e("AutoMessageApplication", "AUTO_MSG: FGS SecurityException, falling back to poller", e)
            try { scheduleCallLogPoller() } catch (_: Exception) {}
        } catch (e: IllegalStateException) {
            Log.e("AutoMessageApplication", "AUTO_MSG: FGS not allowed in background, falling back to poller", e)
            try { scheduleCallLogPoller() } catch (_: Exception) {}
        } catch (e: Exception) {
            Log.e("AutoMessageApplication", "AUTO_MSG: Failed to start CallDetectionService", e)
        }
    }

    private fun isAppInForeground(): Boolean {
        return try {
            val am = getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            am.runningAppProcesses?.any { it.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && it.processName == packageName } == true
        } catch (_: Exception) { false }
    }

    private fun scheduleCallLogPoller() {
        val constraints = Constraints.Builder().build()
        val request = PeriodicWorkRequestBuilder<CallLogPollerWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag("CallLogPollerWork")
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "CallLogPollerWork",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}
