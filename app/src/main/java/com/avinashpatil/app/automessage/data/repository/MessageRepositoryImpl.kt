package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.dao.CustomMessageDao
import com.avinashpatil.app.automessage.data.entity.CustomMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val customMessageDao: CustomMessageDao
) : MessageRepository {
    
    override fun getAllMessages(): Flow<List<CustomMessageEntity>> {
        return customMessageDao.getAllMessages()
    }
    
    override suspend fun getMessageById(messageId: Long): CustomMessageEntity? {
        return customMessageDao.getMessageById(messageId)
    }
    
    override suspend fun getDefaultMessage(): CustomMessageEntity? {
        return customMessageDao.getDefaultMessage()
    }
    
    override suspend fun getMessageByGroupType(groupType: String): CustomMessageEntity? {
        return customMessageDao.getMessageByGroupType(groupType)
    }
    
    override suspend fun insertMessage(message: CustomMessageEntity): Long {
        return customMessageDao.insertMessage(message)
    }
    
    override suspend fun updateMessage(message: CustomMessageEntity) {
        customMessageDao.updateMessage(message)
    }
    
    override suspend fun deleteMessage(messageId: Long) {
        customMessageDao.deleteMessage(customMessageDao.getMessageById(messageId)!!)
    }
    
    override suspend fun setAsDefault(messageId: Long) {
        customMessageDao.clearDefaultMessages()
        customMessageDao.setAsDefault(messageId)
    }
    
    override suspend fun clearDefaultMessages() {
        customMessageDao.clearDefaultMessages()
    }
    
    override suspend fun getMessageCount(): Int {
        return customMessageDao.getMessageCount()
    }
}