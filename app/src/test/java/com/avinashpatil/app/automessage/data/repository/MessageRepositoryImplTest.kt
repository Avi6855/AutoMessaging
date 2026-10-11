package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.dao.CustomMessageDao
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Setting a default template must leave exactly one default behind, and delete must remove the row
 * the DAO returns (not a stale copy). Both are the kind of bug that shows up as "wrong reply sent".
 */
class MessageRepositoryImplTest {

    private val dao = mockk<CustomMessageDao>()
    private val repository = MessageRepositoryImpl(dao)

    private fun message(id: Long, isDefault: Boolean = false) = CustomMessageEntity(
        id = id,
        title = "Busy",
        body = "I will call you back.",
        isDefault = isDefault
    )

    @Test
    fun `setting a default clears the previous one first`() = runTest {
        coEvery { dao.clearDefaultMessages() } returns Unit
        coEvery { dao.setAsDefault(42L) } returns Unit

        repository.setAsDefault(42L)

        coVerifyOrder {
            dao.clearDefaultMessages()
            dao.setAsDefault(42L)
        }
    }

    @Test
    fun `delete removes the entity returned by the dao`() = runTest {
        val stored = message(7)
        coEvery { dao.getMessageById(7L) } returns stored
        coEvery { dao.deleteMessage(stored) } returns Unit

        repository.deleteMessage(7L)

        coVerify { dao.deleteMessage(stored) }
    }

    @Test
    fun `insert returns the generated id`() = runTest {
        coEvery { dao.insertMessage(any()) } returns 99L

        assertEquals(99L, repository.insertMessage(message(0)))
    }

    @Test
    fun `default message lookup is delegated to the dao`() = runTest {
        val fallback = message(1, isDefault = true)
        coEvery { dao.getDefaultMessage() } returns fallback

        assertEquals(fallback, repository.getDefaultMessage())
    }
}
