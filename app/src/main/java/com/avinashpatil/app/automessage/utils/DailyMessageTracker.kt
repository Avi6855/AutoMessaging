package com.avinashpatil.app.automessage.utils

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DailyMessageTracker {
    private const val PREFS_NAME = "daily_message_tracker"
    private const val KEY_PREFIX = "last_sent_"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun hasSentToday(context: Context, phoneNumber: String): Boolean {
        val prefs = getPreferences(context)
        val lastSentDate = prefs.getString(KEY_PREFIX + phoneNumber, "") ?: ""
        val today = dateFormat.format(Date())
        return lastSentDate == today
    }
    
    fun markSentToday(context: Context, phoneNumber: String) {
        val prefs = getPreferences(context)
        val today = dateFormat.format(Date())
        prefs.edit().putString(KEY_PREFIX + phoneNumber, today).apply()
    }
    
    fun clearSentHistory(context: Context, phoneNumber: String) {
        val prefs = getPreferences(context)
        prefs.edit().remove(KEY_PREFIX + phoneNumber).apply()
    }
    
    fun clearAllHistory(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().clear().apply()
    }
    
    fun getLastSentDate(context: Context, phoneNumber: String): String? {
        val prefs = getPreferences(context)
        return prefs.getString(KEY_PREFIX + phoneNumber, null)
    }
}