package com.avinashpatil.app.automessage.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.avinashpatil.app.automessage.service.CallDetectionService
import com.avinashpatil.app.automessage.utils.AutoMessagingStateChecker

class KeepAliveReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "KeepAliveReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val enabled = AutoMessagingStateChecker.isAutoMessagingEnabled(context)
        Log.d(TAG, "AUTO_MSG: keepalive received, autoMessaging=$enabled")

        if (!enabled) {
            Log.d(TAG, "AUTO_MSG: recovery skipped because automation disabled")
            return
        }

        Log.d(TAG, "KeepAlive ping received: ${intent?.action}")
        try {
            val serviceIntent = Intent(context, CallDetectionService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } catch (e: Exception) {
                try {
                    val am = context.getSystemService(android.app.AlarmManager::class.java)
                    val pi = android.app.PendingIntent.getBroadcast(
                        context,
                        1002,
                        Intent("com.avinashpatil.app.automessage.ACTION_KEEPALIVE").setPackage(context.packageName),
                        android.app.PendingIntent.FLAG_UPDATE_CURRENT or pendingFlags()
                    )

                    val triggerAt = System.currentTimeMillis() + 10_000
                    val canExact = try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) am.canScheduleExactAlarms() else true
                    } catch (_: Throwable) { false }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (canExact) {
                            am.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
                        } else {
                            am.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
                        }
                    } else {
                        am.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
                    }
                } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to (re)start CallDetectionService", e)
        }
    }

    private fun pendingFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) android.app.PendingIntent.FLAG_IMMUTABLE else 0
    }
}
