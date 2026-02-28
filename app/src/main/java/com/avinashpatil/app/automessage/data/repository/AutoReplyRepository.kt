package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.data.entity.LastSeenCallEntity
import kotlinx.coroutines.flow.Flow

interface AutoReplyRepository {
    suspend fun getLastSeenCall(): LastSeenCallEntity?
    suspend fun updateLastSeenCall(callId: String, contactId: String?)
    suspend fun logAutoReply(log: AutoReplyLogEntity)
    suspend fun logAutoReplyReturnId(log: AutoReplyLogEntity): Long
    fun getAutoReplyHistory(): Flow<List<AutoReplyLogEntity>>
    fun getAutoReplyHistoryByContact(contactId: String): Flow<List<AutoReplyLogEntity>>
    fun getAutoReplyHistoryByDateRange(startTime: Long, endTime: Long): Flow<List<AutoReplyLogEntity>>

    fun getAutoReplyHistoryByPhone(phone: String): Flow<List<AutoReplyLogEntity>>

    suspend fun getSuccessfulAutoRepliesByPhoneInRange(phone: String, startTime: Long, endTime: Long): Int
    
    fun searchAutoReplyHistory(query: String): Flow<List<AutoReplyLogEntity>>
    suspend fun deleteAutoReplyLog(log: AutoReplyLogEntity)
    suspend fun deleteOldAutoReplyLogs(cutoffTime: Long)
    suspend fun deleteAllAutoReplyLogs()
    suspend fun getAutoReplyLogCount(): Int
    suspend fun hasRecentReply(contactId: String): Boolean
    // Fetch single log by id for detail view
    suspend fun getAutoReplyLogById(id: Long): AutoReplyLogEntity?
    // Delivery status updates
    suspend fun markLogSent(id: Long, attempts: Int, error: String? = null, sentTs: Long)
    suspend fun markLogFailed(id: Long, attempts: Int, error: String?)
    suspend fun markLogDelivered(id: Long, deliveredTs: Long)
    // NEW: Duplicate prevention helper
    suspend fun getLogByPhoneAndDay(phone: String, dayKey: String): AutoReplyLogEntity?

    // Bulk status convert
    suspend fun markAllAsDelivered(deliveredTs: Long)
}
