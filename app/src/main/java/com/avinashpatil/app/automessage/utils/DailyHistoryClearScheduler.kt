package com.avinashpatil.app.automessage.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.avinashpatil.app.automessage.receiver.DailyHistoryClearReceiver
import java.time.ZoneId
import java.time.ZonedDateTime

object DailyHistoryClearScheduler {
    private const val TAG = "DailyHistoryClearScheduler"
    private const val REQUEST_CODE = 9001
    const val ACTION_DAILY_CLEAR = "com.avinashpatil.app.automessage.ACTION_DAILY_HISTORY_CLEAR"
    private val KOLKATA_ZONE: ZoneId = ZoneId.of("Asia/Kolkata")

    fun scheduleDailyClear(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DailyHistoryClearReceiver::class.java).apply {
                action = ACTION_DAILY_CLEAR
            }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)

            val now = ZonedDateTime.now(KOLKATA_ZONE)
            var next = now.withHour(0).withMinute(1).withSecond(0).withNano(0)
            if (!next.isAfter(now)) {
                next = next.plusDays(1)
            }
            val triggerAtMs = next.toInstant().toEpochMilli()

            Log.d(TAG, "Scheduling daily history clear at $next IST (triggerAtMs=$triggerAtMs)")

            // Check exact alarm permission on Android 12+
            val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else true

            if (canExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pendingIntent)
                } else {
                    @Suppress("DEPRECATION")
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMs, pendingIntent)
                }
                Log.d(TAG, "Exact alarm scheduled for 00:01 IST")
            } else {
                // Fallback to inexact but still allow while idle
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMs, pendingIntent)
                } else {
                    @Suppress("DEPRECATION")
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMs, pendingIntent)
                }
                Log.w(TAG, "Exact alarm permission denied — using setAndAllowWhileIdle fallback (may be ~15min inexact). Prompt user to enable Alarms & reminders.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule daily history clear", e)
        }
    }

    fun cancelDailyClear(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DailyHistoryClearReceiver::class.java).apply {
                action = ACTION_DAILY_CLEAR
            }
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
            val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
            alarmManager.cancel(pendingIntent)
            Log.d(TAG, "Daily history clear alarm cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel daily history clear", e)
        }
    }

    fun getKolkataZone(): ZoneId = KOLKATA_ZONE

    fun getTodayDayKey(): String {
        val now = ZonedDateTime.now(KOLKATA_ZONE)
        return String.format("%04d-%02d-%02d", now.year, now.monthValue, now.dayOfMonth)
    }
}
