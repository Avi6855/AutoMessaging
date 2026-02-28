package com.avinashpatil.app.automessage.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.os.Build
import android.app.AlarmManager
import android.app.PendingIntent

class PhoneStateReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("PhoneStateReceiver", "Received intent: ${intent.action}")
        // Ensure work completes if the system kills the receiver early
        val pendingResult = goAsync()
        try {
        
        when (intent.action) {
            TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                android.util.Log.d("PhoneStateReceiver", "Call state changed: $state")
                val serviceIntent = Intent(context, CallDetectionService::class.java).apply {
                    action = "CALL_STATE_CHANGED"
                    putExtra("state", state)
                }
                startServiceSafely(context, serviceIntent)
            }
            
            "android.intent.action.NEW_OUTGOING_CALL" -> {
                val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
                
                android.util.Log.d("PhoneStateReceiver", "New outgoing call: $phoneNumber")

                // Always start the service; it can resolve number if null
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
        // On Android 12+ exact alarms need special permission. If not granted, fall back to inexact.
        try {
            val am = context.getSystemService(AlarmManager::class.java)
            val pi = PendingIntent.getBroadcast(
                context,
                1001,
                Intent("com.avinashpatil.app.automessage.ACTION_KEEPALIVE").setPackage(context.packageName),
                PendingIntent.FLAG_UPDATE_CURRENT or pendingFlags()
            )

            val triggerAt = System.currentTimeMillis() + 3_000

            val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try { am.canScheduleExactAlarms() } catch (_: Throwable) { false }
            } else true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canExact) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                    android.util.Log.d("PhoneStateReceiver", "Exact keepalive scheduled (~3s)")
                } else {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                    android.util.Log.d("PhoneStateReceiver", "Inexact keepalive scheduled (~3s) due to missing exact alarm permission")
                }
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                android.util.Log.d("PhoneStateReceiver", "Exact keepalive scheduled (pre-M)")
            }
        } catch (e: SecurityException) {
            // Final fallback: schedule inexact alarm without while-idle if even allowWhileIdle fails
            try {
                val am = context.getSystemService(AlarmManager::class.java)
                val pi = PendingIntent.getBroadcast(
                    context,
                    1001,
                    Intent("com.avinashpatil.app.automessage.ACTION_KEEPALIVE").setPackage(context.packageName),
                    PendingIntent.FLAG_UPDATE_CURRENT or pendingFlags()
                )
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5_000, pi)
                android.util.Log.w("PhoneStateReceiver", "SecurityException, used non-exact alarm fallback (~5s)")
            } catch (t: Throwable) {
                android.util.Log.e("PhoneStateReceiver", "Failed to schedule keepalive (fallback)", t)
            }
        } catch (t: Throwable) {
            android.util.Log.e("PhoneStateReceiver", "Failed to schedule keepalive", t)
        }
    }

    private fun pendingFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    }
}
