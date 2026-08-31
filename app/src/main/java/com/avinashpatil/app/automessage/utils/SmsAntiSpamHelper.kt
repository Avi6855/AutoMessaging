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

    fun jitterDelayMs(): Long = Random.nextLong(3000L, 7000L)

    /**
     * Personalize message to avoid identical payload spam detection.
     * - Replaces {name} with contact name if present
     * - Appends lightweight random suffix to break hash equality (visible but minimal)
     * - Appends STOP footer for compliance
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

        // If message already contains STOP footer, don't duplicate
        val hasFooter = msg.contains("STOP", ignoreCase = true)

        // Lightweight variation suffix (breaks identical hash without being spammy)
        // Use short random ref — visible but professional
        val suffixes = listOf("", " — Thanks!", " — Team", "")
        val suffix = suffixes.random()

        if (suffix.isNotEmpty() && !msg.endsWith(suffix)) {
            msg = "$msg$suffix"
        }

        // Append compliance footer if missing (helps carriers treat as solicited)
        if (!hasFooter) {
            msg = "$msg\nReply STOP to opt out"
        }

        // Add tiny random ref to guarantee uniqueness when sending same base to many numbers in bulk
        // Use last 4 of nanoTime hex — minimal, 5 chars like " [a3f9]"
        val ref = Integer.toHexString((System.nanoTime() and 0xFFFF).toInt()).padStart(4, '0')
        // Only add ref for bulk-like context where same base repeats; keep it subtle
        // We add as zero-width? Better visible tiny tag — carriers ignore but breaks exact dup filter
        // Keep it commented to avoid user-visible noise; enable only if spam persists:
        // msg = "$msg [$ref]"

        return msg.trim()
    }

    /**
     * Full anti-spam wrapped message — call before every send.
     */
    fun prepareMessage(base: String, contact: ContactEntity?): String {
        return personalize(base, contact)
    }
}
