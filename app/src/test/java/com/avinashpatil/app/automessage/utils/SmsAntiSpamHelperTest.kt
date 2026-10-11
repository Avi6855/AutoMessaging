package com.avinashpatil.app.automessage.utils

import com.avinashpatil.app.automessage.data.entity.ContactEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The message that actually goes out on the wire is built here. The app promises two things:
 * a saved template is never silently rewritten, and {name} is filled in when the caller is known.
 */
class SmsAntiSpamHelperTest {

    private fun contact(name: String?) = ContactEntity(
        id = "contact-1",
        name = name.orEmpty(),
        phoneNumber = "+919860080257"
    )

    @Test
    fun `template is sent exactly as the user saved it`() {
        val template = "Sorry, I am in a meeting. Will call you back soon."

        assertEquals(template, SmsAntiSpamHelper.personalize(template, contact("Rahul")))
        assertEquals(template, SmsAntiSpamHelper.personalize(template, null))
    }

    @Test
    fun `name placeholder is replaced when the caller is known`() {
        val result = SmsAntiSpamHelper.personalize("Hi {name}, I will call you back shortly.", contact("Rahul"))

        assertEquals("Hi Rahul, I will call you back shortly.", result)
    }

    @Test
    fun `placeholder is replaced regardless of casing`() {
        val result = SmsAntiSpamHelper.personalize("Hi {NAME}, busy right now.", contact("Rahul"))

        assertEquals("Hi Rahul, busy right now.", result)
    }

    @Test
    fun `placeholder is dropped cleanly for an unknown caller`() {
        assertEquals("Hi , busy right now.", SmsAntiSpamHelper.personalize("Hi {name}, busy right now.", null))
        assertEquals("", SmsAntiSpamHelper.personalize("{name}", null))
    }

    @Test
    fun `duplicate spaces left by the placeholder are collapsed`() {
        val result = SmsAntiSpamHelper.personalize("Hi {name},  call later.", null)

        assertEquals("Hi , call later.", result)
    }

    @Test
    fun `blank templates stay blank`() {
        assertEquals("", SmsAntiSpamHelper.personalize("   ", contact("Rahul")))
    }

    @Test
    fun `prepareMessage and personalize agree`() {
        val template = "Hi {name}, driving right now."

        assertEquals(
            SmsAntiSpamHelper.personalize(template, contact("Rahul")),
            SmsAntiSpamHelper.prepareMessage(template, contact("Rahul"))
        )
    }

    @Test
    fun `jitter stays inside the configured send window`() {
        repeat(500) {
            val delay = SmsAntiSpamHelper.jitterDelayMs()
            assertTrue("delay was $delay", delay in 3_000L..6_999L)
        }
    }

    @Test
    fun `carrier safe rate limits are pinned`() {
        assertEquals(30, SmsAntiSpamHelper.MAX_PER_HOUR)
        assertEquals(100, SmsAntiSpamHelper.MAX_PER_DAY)
    }
}
