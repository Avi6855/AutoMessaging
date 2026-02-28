package com.avinashpatil.app.automessage.data.dao

import androidx.room.*
import com.avinashpatil.app.automessage.data.entity.DiscrepancyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscrepancyLogDao {
    @Query("SELECT * FROM discrepancy_logs ORDER BY createdAt DESC")
    fun getAll(): Flow<List<DiscrepancyLogEntity>>

    @Query("SELECT * FROM discrepancy_logs WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<DiscrepancyLogEntity>>

    @Query("SELECT * FROM discrepancy_logs WHERE phoneNumber = :phone ORDER BY createdAt DESC")
    fun getByPhone(phone: String): Flow<List<DiscrepancyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: DiscrepancyLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<DiscrepancyLogEntity>)

    @Update
    suspend fun update(log: DiscrepancyLogEntity)

    @Delete
    suspend fun delete(log: DiscrepancyLogEntity)

    @Query("DELETE FROM discrepancy_logs WHERE createdAt < :cutoff")
    suspend fun deleteOld(cutoff: Long)

    @Query("UPDATE discrepancy_logs SET status = :status, resolvedAt = :resolvedAt, notes = :notes WHERE id = :id")
    suspend fun markResolved(id: Long, status: String = "RESOLVED", resolvedAt: Long = System.currentTimeMillis(), notes: String? = null)
}