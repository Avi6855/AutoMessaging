package com.avinashpatil.app.automessage.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.os.Build
import android.app.AlarmManager
import android.app.PendingIntent
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
                    Log.d(TAG, "Exact keepalive scheduled (~3s)")
                } else {
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                    Log.d(TAG, "Inexact keepalive scheduled (~3s)")
                }
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi)
                Log.d(TAG, "Exact keepalive scheduled (pre-M)")
            }
        } catch (e: SecurityException) {
            try {
                val am = context.getSystemService(AlarmManager::class.java)
                val pi = PendingIntent.getBroadcast(
                    context,
                    1001,
                    Intent("com.avinashpatil.app.automessage.ACTION_KEEPALIVE").setPackage(context.packageName),
                    PendingIntent.FLAG_UPDATE_CURRENT or pendingFlags()
                )
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5_000, pi)
                Log.w(TAG, "SecurityException, used non-exact alarm fallback (~5s)")
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to schedule keepalive (fallback)", t)
            }
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to schedule keepalive", t)
        }
    }

    private fun pendingFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    }
}
