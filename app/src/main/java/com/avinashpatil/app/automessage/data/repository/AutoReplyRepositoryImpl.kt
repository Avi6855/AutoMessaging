package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.dao.AutoReplyLogDao
import com.avinashpatil.app.automessage.data.dao.LastSeenCallDao
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.data.entity.LastSeenCallEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoReplyRepositoryImpl @Inject constructor(
    private val autoReplyLogDao: AutoReplyLogDao,
    private val lastSeenCallDao: LastSeenCallDao
) : AutoReplyRepository {

    override suspend fun getLastSeenCall(): LastSeenCallEntity? {
        return lastSeenCallDao.getLastSeenCall()
    }

    override suspend fun updateLastSeenCall(callId: String, contactId: String?) {
        lastSeenCallDao.insertLastSeenCall(LastSeenCallEntity(callId = callId, contactId = contactId))
    }

    override suspend fun logAutoReply(log: AutoReplyLogEntity) {
        autoReplyLogDao.insertLog(log)
    }

    override suspend fun logAutoReplyReturnId(log: AutoReplyLogEntity): Long {
        return autoReplyLogDao.insertLogReturnId(log)
    }

    override fun getAutoReplyHistory(): Flow<List<AutoReplyLogEntity>> {
        return autoReplyLogDao.getAllLogs()
    }
    
    override fun getAutoReplyHistoryByContact(contactId: String): Flow<List<AutoReplyLogEntity>> {
        return autoReplyLogDao.getLogsByContact(contactId)
    }
    
    override fun getAutoReplyHistoryByDateRange(startTime: Long, endTime: Long): Flow<List<AutoReplyLogEntity>> {
        return autoReplyLogDao.getLogsByDateRange(startTime, endTime)
    }

    override fun getAutoReplyHistoryByPhone(phone: String): Flow<List<AutoReplyLogEntity>> {
        return autoReplyLogDao.getLogsByPhone(phone)
    }

    override suspend fun getSuccessfulAutoRepliesByPhoneInRange(phone: String, startTime: Long, endTime: Long): Int {
        return autoReplyLogDao.getSuccessfulCountByPhoneInRange(phone, startTime, endTime)
    }

    override fun searchAutoReplyHistory(query: String): Flow<List<AutoReplyLogEntity>> {
        return autoReplyLogDao.searchLogs(query)
    }
    
    override suspend fun deleteAutoReplyLog(log: AutoReplyLogEntity) {
        autoReplyLogDao.deleteLog(log)
    }
    
    override suspend fun deleteOldAutoReplyLogs(cutoffTime: Long) {
        autoReplyLogDao.deleteOldLogs(cutoffTime)
    }
    
    override suspend fun deleteAllAutoReplyLogs() {
        autoReplyLogDao.deleteAllLogs()
    }
    
    override suspend fun getAutoReplyLogCount(): Int {
        return autoReplyLogDao.getLogCount()
    }
    
    override suspend fun hasRecentReply(contactId: String): Boolean {
        return autoReplyLogDao.getLogCountByContact(contactId) > 0
    }

    // Fetch single log by id for detail view
    override suspend fun getAutoReplyLogById(id: Long): AutoReplyLogEntity? {
        return autoReplyLogDao.getLogById(id)
    }

    override suspend fun markLogSent(id: Long, attempts: Int, error: String?, sentTs: Long) {
        autoReplyLogDao.markLogSent(id = id, attempts = attempts, error = error, sentTs = sentTs)
    }

    override suspend fun markLogFailed(id: Long, attempts: Int, error: String?) {
        autoReplyLogDao.markLogFailed(id = id, attempts = attempts, error = error)
    }

    override suspend fun markLogDelivered(id: Long, deliveredTs: Long) {
        autoReplyLogDao.markLogDelivered(id = id, deliveredTs = deliveredTs)
    }

    // NEW: Duplicate prevention helper
    override suspend fun getLogByPhoneAndDay(phone: String, dayKey: String): AutoReplyLogEntity? {
        return autoReplyLogDao.getLogByPhoneAndDay(phone, dayKey)
    }

    override suspend fun markAllAsDelivered(deliveredTs: Long) {
        autoReplyLogDao.convertAllToDelivered(deliveredTs)
    }
}
