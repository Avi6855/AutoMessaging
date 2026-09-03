package com.avinashpatil.app.automessage.utils

/**
 * Single place for phone-number normalization.
 * Indian numbers arrive as 9860080257, +919860080257, 09860080257 — all must map to one key
 * so once-per-day / duplicate / recent-sent checks work across formats.
 */
object PhoneNumberUtils {
    fun normalize(phoneNumber: String?): String {
        if (phoneNumber.isNullOrBlank()) return ""
        val digits = phoneNumber.filter { it.isDigit() }
        if (digits.isEmpty()) return ""
        // Keep last 10 digits (Indian mobile), fallback to full digits for short codes
        return if (digits.length > 10) digits.takeLast(10) else digits
    }

    fun matches(a: String?, b: String?): Boolean {
        if (a.isNullOrBlank() || b.isNullOrBlank()) return false
        val na = normalize(a)
        val nb = normalize(b)
        if (na.isEmpty() || nb.isEmpty()) return false
        return na == nb || na.endsWith(nb) || nb.endsWith(na)
    }
}
