package com.avinashpatil.app.automessage.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.avinashpatil.app.automessage.utils.AutoMessagingStateChecker
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val enabled = AutoMessagingStateChecker.isAutoMessagingEnabled(context)
            Log.d(TAG, "AUTO_MSG: boot received, autoMessaging=$enabled")

            if (!enabled) {
                Log.d(TAG, "AUTO_MSG: recovery skipped because automation disabled")
                return
            }

            try {
                val serviceIntent = Intent(context, CallDetectionService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start CallDetectionService on boot", e)
            }
            try {
                scheduleCallLogPoller(context)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to schedule CallLogPollerWorker on boot", e)
            }
        }
    }

    private fun scheduleCallLogPoller(context: Context) {
        val constraints = Constraints.Builder().build()
        val request = PeriodicWorkRequestBuilder<CallLogPollerWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag("CallLogPollerWork")
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "CallLogPollerWork",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}
