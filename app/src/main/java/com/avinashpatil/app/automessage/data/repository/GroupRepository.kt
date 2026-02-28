package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getAllGroups(): Flow<List<GroupEntity>>
    suspend fun getGroupById(groupId: Long): GroupEntity?
    suspend fun getGroupByName(name: String): GroupEntity?
    suspend fun insertGroup(group: GroupEntity): Long
    suspend fun updateGroup(group: GroupEntity)
    suspend fun deleteGroup(groupId: Long)
    suspend fun updateGroupDefaultMessage(groupId: Long, messageId: Long)
    suspend fun getGroupCount(): Int
}