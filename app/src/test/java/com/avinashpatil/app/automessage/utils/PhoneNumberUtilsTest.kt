package com.avinashpatil.app.automessage.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Every duplicate / once-per-day / blacklist check keys off the normalized number. If two formats of
 * the same caller normalize differently, the caller gets spammed with replies — so all the formats
 * that Indian carriers hand us are covered here.
 */
class PhoneNumberUtilsTest {

    private val expected = "9860080257"

    @Test
    fun `all indian formats collapse to the same key`() {
        assertEquals(expected, PhoneNumberUtils.normalize("9860080257"))
        assertEquals(expected, PhoneNumberUtils.normalize("+919860080257"))
        assertEquals(expected, PhoneNumberUtils.normalize("09860080257"))
        assertEquals(expected, PhoneNumberUtils.normalize("+91 98600 80257"))
        assertEquals(expected, PhoneNumberUtils.normalize("0091-9860080257"))
        assertEquals(expected, PhoneNumberUtils.normalize("+91 (98600) 80257"))
    }

    @Test
    fun `numbers shorter than ten digits are kept as they are`() {
        assertEquals("12345", PhoneNumberUtils.normalize("12345"))
        assertEquals("555", PhoneNumberUtils.normalize("555"))
    }

    @Test
    fun `longer numbers keep the last ten digits`() {
        // 15 digits with extra prefixes: everything before the final 10 digits is dropped
        assertEquals("0080257123", PhoneNumberUtils.normalize("919860080257123"))
        assertEquals("1234567890", PhoneNumberUtils.normalize("001234567890"))
    }

    @Test
    fun `blank, null and digit free inputs normalize to empty`() {
        assertEquals("", PhoneNumberUtils.normalize(null))
        assertEquals("", PhoneNumberUtils.normalize(""))
        assertEquals("", PhoneNumberUtils.normalize("   "))
        assertEquals("", PhoneNumberUtils.normalize("+ - ()"))
        assertEquals("", PhoneNumberUtils.normalize("unknown"))
    }

    @Test
    fun `matches ignores the country code and spacing`() {
        assertTrue(PhoneNumberUtils.matches("+91 98600 80257", "09860080257"))
        assertTrue(PhoneNumberUtils.matches("9860080257", "9860080257"))
        assertTrue(PhoneNumberUtils.matches("+919860080257", "9860080257"))
    }

    @Test
    fun `matches is false for different or unusable numbers`() {
        assertFalse(PhoneNumberUtils.matches("9860080257", "9860080258"))
        assertFalse(PhoneNumberUtils.matches(null, "9860080257"))
        assertFalse(PhoneNumberUtils.matches("", ""))
        assertFalse(PhoneNumberUtils.matches("unknown", "9860080257"))
    }

    @Test
    fun `partially stored numbers still match by suffix`() {
        assertTrue(PhoneNumberUtils.matches("80257", "9860080257"))
        assertTrue(PhoneNumberUtils.matches("9860080257", "80257"))
    }
}
