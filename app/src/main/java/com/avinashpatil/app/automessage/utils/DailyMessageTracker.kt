package com.avinashpatil.app.automessage.utils

import android.content.Context
import android.content.SharedPreferences
import java.time.ZoneId

object DailyMessageTracker {
    private const val PREFS_NAME = "daily_message_tracker"
    private const val KEY_PREFIX = "last_sent_"
    private val KOLKATA_ZONE: ZoneId = ZoneId.of("Asia/Kolkata")

    private fun todayKey(): String {
        val now = java.time.ZonedDateTime.now(KOLKATA_ZONE)
        return String.format("%04d-%02d-%02d", now.year, now.monthValue, now.dayOfMonth)
    }
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun hasSentToday(context: Context, phoneNumber: String): Boolean {
        val prefs = getPreferences(context)
        val lastSentDate = prefs.getString(KEY_PREFIX + PhoneNumberUtils.normalize(phoneNumber), "") ?: ""
        val today = todayKey()
        return lastSentDate == today
    }

    fun markSentToday(context: Context, phoneNumber: String) {
        val prefs = getPreferences(context)
        val today = todayKey()
        prefs.edit().putString(KEY_PREFIX + PhoneNumberUtils.normalize(phoneNumber), today).apply()
    }

    fun clearSentHistory(context: Context, phoneNumber: String) {
        val prefs = getPreferences(context)
        prefs.edit().remove(KEY_PREFIX + PhoneNumberUtils.normalize(phoneNumber)).apply()
    }
    
    fun clearAllHistory(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().clear().apply()
    }
    
    fun getLastSentDate(context: Context, phoneNumber: String): String? {
        val prefs = getPreferences(context)
        return prefs.getString(KEY_PREFIX + PhoneNumberUtils.normalize(phoneNumber), null)
    }
}