package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.entity.DiscrepancyLogEntity
import kotlinx.coroutines.flow.Flow

interface DiscrepancyRepository {
    fun getAll(): Flow<List<DiscrepancyLogEntity>>
    fun getByStatus(status: String): Flow<List<DiscrepancyLogEntity>>
    fun getByPhone(phone: String): Flow<List<DiscrepancyLogEntity>>
    suspend fun insert(log: DiscrepancyLogEntity): Long
    suspend fun insertAll(logs: List<DiscrepancyLogEntity>)
    suspend fun markResolved(id: Long, status: String = "RESOLVED", notes: String? = null)
    suspend fun deleteOld(cutoff: Long)
}