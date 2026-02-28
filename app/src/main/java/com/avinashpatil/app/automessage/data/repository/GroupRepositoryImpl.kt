package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.dao.GroupDao
import com.avinashpatil.app.automessage.data.entity.GroupEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao
) : GroupRepository {
    
    override fun getAllGroups(): Flow<List<GroupEntity>> {
        return groupDao.getAllGroups()
    }
    
    override suspend fun getGroupById(groupId: Long): GroupEntity? {
        return groupDao.getGroupById(groupId)
    }
    
    override suspend fun getGroupByName(name: String): GroupEntity? {
        return groupDao.getGroupByName(name)
    }
    
    override suspend fun insertGroup(group: GroupEntity): Long {
        return groupDao.insertGroup(group)
    }
    
    override suspend fun updateGroup(group: GroupEntity) {
        groupDao.updateGroup(group)
    }
    
    override suspend fun deleteGroup(groupId: Long) {
        groupDao.getGroupById(groupId)?.let {
            groupDao.deleteGroup(it)
        }
    }
    
    override suspend fun updateGroupDefaultMessage(groupId: Long, messageId: Long) {
        groupDao.updateGroupMessage(groupId, messageId)
    }
    
    override suspend fun getGroupCount(): Int {
        return groupDao.getGroupCount()
    }
}