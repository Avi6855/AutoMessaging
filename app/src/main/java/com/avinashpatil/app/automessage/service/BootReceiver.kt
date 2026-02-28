package com.avinashpatil.app.automessage.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            try {
                val serviceIntent = Intent(context, CallDetectionService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } catch (e: Exception) {
                android.util.Log.e("BootReceiver", "Failed to start CallDetectionService on boot", e)
            }
            try {
                scheduleCallLogPoller(context)
            } catch (e: Exception) {
                android.util.Log.e("BootReceiver", "Failed to schedule CallLogPollerWorker on boot", e)
            }
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

