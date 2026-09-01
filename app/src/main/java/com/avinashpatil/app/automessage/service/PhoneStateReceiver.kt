package com.avinashpatil.app.automessage.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.os.Build
import android.util.Log
import com.avinashpatil.app.automessage.utils.AutoMessagingStateChecker

class PhoneStateReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "PhoneStateReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val enabled = AutoMessagingStateChecker.isAutoMessagingEnabled(context)
        Log.d(TAG, "AUTO_MSG: phone state received, autoMessaging=$enabled")

        if (!enabled) {
            Log.d(TAG, "AUTO_MSG: phone state ignored because automation disabled")
            return
        }

        Log.d(TAG, "Received intent: ${intent.action}")
        val pendingResult = goAsync()
        try {
            when (intent.action) {
                TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                    val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                    Log.d(TAG, "Call state changed: $state")
                    val serviceIntent = Intent(context, CallDetectionService::class.java).apply {
                        action = "CALL_STATE_CHANGED"
                        putExtra("state", state)
                    }
                    startServiceSafely(context, serviceIntent)
                }

                "android.intent.action.NEW_OUTGOING_CALL" -> {
                    val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                    Log.d(TAG, "New outgoing call: $phoneNumber")
                    val serviceIntent = Intent(context, CallDetectionService::class.java).apply {
                        action = "NEW_OUTGOING_CALL"
                        putExtra("phone_number", phoneNumber)
                    }
                    startServiceSafely(context, serviceIntent)
                }
            }
        } finally {
            try { pendingResult.finish() } catch (_: Exception) {}
        }
    }

    private fun startServiceSafely(context: Context, serviceIntent: Intent) {
        // Try to start the service directly first
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d(TAG, "Service started directly with intent action=${serviceIntent.action}")
            return
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service directly: ${e.message}")
        }

        // Fallback: try starting the service without extras (just to get it alive)
        try {
            val plainIntent = Intent(context, CallDetectionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(plainIntent)
            } else {
                context.startService(plainIntent)
            }
            Log.d(TAG, "Service started (plain fallback)")
            return
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start service (plain fallback): ${e.message}")
        }

        // Last resort: schedule an alarm to restart the service in 3 seconds
        try {
            val am = context.getSystemService(android.app.AlarmManager::class.java)
            val pi = PendingIntent.getBroadcast(
                context,
                1001,
                Intent("com.avinashpatil.app.automessage.ACTION_KEEPALIVE").setPackage(context.packageName),
                PendingIntent.FLAG_UPDATE_CURRENT or pendingFlags()
            )
            val triggerAt = System.currentTimeMillis() + 3_000
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am?.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                @Suppress("DEPRECATION")
                am?.set(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
            Log.w(TAG, "Scheduled keepalive alarm fallback (~3s)")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule keepalive alarm", e)
        }
    }

    private fun pendingFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    }
}
