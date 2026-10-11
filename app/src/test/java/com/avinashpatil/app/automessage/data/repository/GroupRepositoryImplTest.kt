package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.dao.GroupDao
import com.avinashpatil.app.automessage.data.entity.GroupEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Deleting a group must never call the DAO with a null entity, and re-pointing a group's default
 * template has to reach the DAO with both ids intact.
 */
class GroupRepositoryImplTest {

    private val dao = mockk<GroupDao>()
    private val repository = GroupRepositoryImpl(dao)

    private fun group(id: Long) = GroupEntity(
        id = id,
        name = "Family",
        description = null,
        defaultMessageId = 3L
    )

    @Test
    fun `delete removes the existing group`() = runTest {
        val stored = group(5)
        coEvery { dao.getGroupById(5L) } returns stored
        coEvery { dao.deleteGroup(stored) } returns Unit

        repository.deleteGroup(5L)

        coVerify { dao.deleteGroup(stored) }
    }

    @Test
    fun `deleting a missing group does nothing`() = runTest {
        coEvery { dao.getGroupById(404L) } returns null

        repository.deleteGroup(404L)

        coVerify(exactly = 0) { dao.deleteGroup(any()) }
    }

    @Test
    fun `default message update passes both ids through`() = runTest {
        coEvery { dao.updateGroupMessage(5L, 9L) } returns Unit

        repository.updateGroupDefaultMessage(5L, 9L)

        coVerify { dao.updateGroupMessage(5L, 9L) }
    }

    @Test
    fun `group count is delegated to the dao`() = runTest {
        coEvery { dao.getGroupCount() } returns 4

        assertEquals(4, repository.getGroupCount())
    }
}
