package com.avinashpatil.app.automessage.data.dao

import androidx.room.*
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoReplyLogDao {
    
    @Query("SELECT * FROM auto_reply_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AutoReplyLogEntity>>
    
    @Query("SELECT * FROM auto_reply_logs WHERE contactId = :contactId ORDER BY timestamp DESC")
    fun getLogsByContact(contactId: String): Flow<List<AutoReplyLogEntity>>
    
    @Query("SELECT * FROM auto_reply_logs WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getLogsByDateRange(startTime: Long, endTime: Long): Flow<List<AutoReplyLogEntity>>

    @Query("SELECT * FROM auto_reply_logs WHERE phoneNumber = :phone ORDER BY timestamp DESC")
    fun getLogsByPhone(phone: String): Flow<List<AutoReplyLogEntity>>

    @Query("SELECT COUNT(*) FROM auto_reply_logs WHERE phoneNumber = :phone AND timestamp BETWEEN :startTime AND :endTime AND status IN ('SENT','DELIVERED')")
    suspend fun getSuccessfulCountByPhoneInRange(phone: String, startTime: Long, endTime: Long): Int
    
    @Query("SELECT * FROM auto_reply_logs WHERE messageText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchLogs(query: String): Flow<List<AutoReplyLogEntity>>
    
    // NEW: Fetch by phone and day for atomic duplicate prevention
    @Query("SELECT * FROM auto_reply_logs WHERE phoneNumber = :phone AND dayKey = :dayKey LIMIT 1")
    suspend fun getLogByPhoneAndDay(phone: String, dayKey: String): AutoReplyLogEntity?
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLog(log: AutoReplyLogEntity)
    
    // Insert that returns row id for tracking delivery updates
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLogReturnId(log: AutoReplyLogEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLogs(logs: List<AutoReplyLogEntity>)
    
    @Delete
    suspend fun deleteLog(log: AutoReplyLogEntity)
    
    @Query("DELETE FROM auto_reply_logs WHERE timestamp < :cutoffTime")
    suspend fun deleteOldLogs(cutoffTime: Long)
    
    @Query("DELETE FROM auto_reply_logs")
    suspend fun deleteAllLogs()
    
    @Query("SELECT COUNT(*) FROM auto_reply_logs")
    suspend fun getLogCount(): Int
    
    @Query("SELECT COUNT(*) FROM auto_reply_logs WHERE contactId = :contactId")
    suspend fun getLogCountByContact(contactId: String): Int

    // Fetch single log by id for detail view
    @Query("SELECT * FROM auto_reply_logs WHERE id = :id LIMIT 1")
    suspend fun getLogById(id: Long): AutoReplyLogEntity?
    
    // Delivery status updates
    @Query("UPDATE auto_reply_logs SET status = :status, attempts = :attempts, error = :error, sentTimestamp = :sentTs WHERE id = :id")
    suspend fun markLogSent(id: Long, status: String = "SENT", attempts: Int, error: String? = null, sentTs: Long)
    
    @Query("UPDATE auto_reply_logs SET status = :status, attempts = :attempts, error = :error WHERE id = :id")
    suspend fun markLogFailed(id: Long, status: String = "FAILED", attempts: Int, error: String?)
    
    @Query("UPDATE auto_reply_logs SET status = :status, deliveredTimestamp = :deliveredTs WHERE id = :id")
    suspend fun markLogDelivered(id: Long, status: String = "DELIVERED", deliveredTs: Long)

    // Convert all Pending/Sent to Delivered immediately after successful send
    @Query("UPDATE auto_reply_logs SET status = 'DELIVERED', deliveredTimestamp = :deliveredTs WHERE status IN ('PENDING','SENT')")
    suspend fun convertAllToDelivered(deliveredTs: Long)
}
