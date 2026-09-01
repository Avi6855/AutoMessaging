package com.avinashpatil.app.automessage.utils

import android.content.Context
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

object SmsAntiSpamHelper {
    private const val PREFS = "sms_antispam_prefs"
    private const val KEY_HOUR_COUNT = "hour_count"
    private const val KEY_HOUR_KEY = "hour_key"
    private const val KEY_DAY_COUNT = "day_count"
    private const val KEY_DAY_KEY = "day_key"
    private val KOLKATA: ZoneId = ZoneId.of("Asia/Kolkata")

    // Carrier-safe limits: adjustable but conservative for TRAI DLT
    const val MAX_PER_HOUR = 30
    const val MAX_PER_DAY = 100

    private fun hourKey(): String {
        val n = ZonedDateTime.now(KOLKATA)
        return String.format("%04d-%02d-%02d-%02d", n.year, n.monthValue, n.dayOfMonth, n.hour)
    }
    private fun dayKey(): String {
        val n = ZonedDateTime.now(KOLKATA)
        return String.format("%04d-%02d-%02d", n.year, n.monthValue, n.dayOfMonth)
    }

    fun canSendNow(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val hk = hourKey()
        val dk = dayKey()
        val storedHourKey = prefs.getString(KEY_HOUR_KEY, "")
        val storedDayKey = prefs.getString(KEY_DAY_KEY, "")
        val hourCount = if (storedHourKey == hk) prefs.getInt(KEY_HOUR_COUNT, 0) else 0
        val dayCount = if (storedDayKey == dk) prefs.getInt(KEY_DAY_COUNT, 0) else 0
        return hourCount < MAX_PER_HOUR && dayCount < MAX_PER_DAY
    }

    fun recordSent(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val hk = hourKey()
        val dk = dayKey()
        val storedHourKey = prefs.getString(KEY_HOUR_KEY, "")
        val storedDayKey = prefs.getString(KEY_DAY_KEY, "")
        var hourCount = if (storedHourKey == hk) prefs.getInt(KEY_HOUR_COUNT, 0) else 0
        var dayCount = if (storedDayKey == dk) prefs.getInt(KEY_DAY_COUNT, 0) else 0
        hourCount += 1
        dayCount += 1
        prefs.edit()
            .putString(KEY_HOUR_KEY, hk)
            .putString(KEY_DAY_KEY, dk)
            .putInt(KEY_HOUR_COUNT, hourCount)
            .putInt(KEY_DAY_COUNT, dayCount)
            .apply()
    }

    @Synchronized
    fun recordSentIfAllowed(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val hk = hourKey()
        val dk = dayKey()
        val storedHourKey = prefs.getString(KEY_HOUR_KEY, "")
        val storedDayKey = prefs.getString(KEY_DAY_KEY, "")
        val hourCount = if (storedHourKey == hk) prefs.getInt(KEY_HOUR_COUNT, 0) else 0
        val dayCount = if (storedDayKey == dk) prefs.getInt(KEY_DAY_COUNT, 0) else 0
        if (hourCount >= MAX_PER_HOUR || dayCount >= MAX_PER_DAY) return false
        prefs.edit()
            .putString(KEY_HOUR_KEY, hk)
            .putString(KEY_DAY_KEY, dk)
            .putInt(KEY_HOUR_COUNT, hourCount + 1)
            .putInt(KEY_DAY_COUNT, dayCount + 1)
            .apply()
        return true
    }

    fun jitterDelayMs(): Long = Random.nextLong(3000L, 7000L)

    /**
     * Personalize message to avoid identical payload spam detection.
     * - Replaces {name} with contact name if present
     * - Sends message EXACTLY as configured by user (no modifications, no footer)
     */
    fun personalize(base: String, contact: ContactEntity?): String {
        var msg = base.trim()
        if (msg.isEmpty()) return msg

        // Interpolate {name}
        val name = contact?.name?.trim().orEmpty()
        if (name.isNotEmpty()) {
            msg = msg.replace("{name}", name, ignoreCase = true)
        } else {
            msg = msg.replace("{name}", "", ignoreCase = true).replace("  ", " ").trim()
        }

        // Send message EXACTLY as configured by user — no modifications, no suffix, no footer
        return msg.trim()
    }

    /**
     * Full anti-spam wrapped message — call before every send.
     */
    fun prepareMessage(base: String, contact: ContactEntity?): String {
        return personalize(base, contact)
    }
}
