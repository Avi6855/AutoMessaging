package com.avinashpatil.app.automessage.data.dao

import androidx.room.*
import com.avinashpatil.app.automessage.data.entity.LastSeenCallEntity

@Dao
interface LastSeenCallDao {
    
    @Query("SELECT * FROM last_seen_calls ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastSeenCall(): LastSeenCallEntity?
    
    @Query("SELECT * FROM last_seen_calls WHERE callId = :callId")
    suspend fun getLastSeenCallById(callId: String): LastSeenCallEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastSeenCall(lastSeenCall: LastSeenCallEntity)
    
    @Update
    suspend fun updateLastSeenCall(lastSeenCall: LastSeenCallEntity)
    
    @Query("DELETE FROM last_seen_calls")
    suspend fun deleteAllLastSeenCalls()
    
    @Query("DELETE FROM last_seen_calls WHERE timestamp < :cutoffTime")
    suspend fun deleteOldLastSeenCalls(cutoffTime: Long)
}