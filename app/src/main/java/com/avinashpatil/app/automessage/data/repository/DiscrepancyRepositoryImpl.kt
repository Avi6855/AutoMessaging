package com.avinashpatil.app.automessage.data.repository

import com.avinashpatil.app.automessage.data.dao.DiscrepancyLogDao
import com.avinashpatil.app.automessage.data.entity.DiscrepancyLogEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscrepancyRepositoryImpl @Inject constructor(
    private val dao: DiscrepancyLogDao
) : DiscrepancyRepository {
    override fun getAll(): Flow<List<DiscrepancyLogEntity>> = dao.getAll()
    override fun getByStatus(status: String): Flow<List<DiscrepancyLogEntity>> = dao.getByStatus(status)
    override fun getByPhone(phone: String): Flow<List<DiscrepancyLogEntity>> = dao.getByPhone(phone)
    override suspend fun insert(log: DiscrepancyLogEntity): Long = dao.insert(log)
    override suspend fun insertAll(logs: List<DiscrepancyLogEntity>) = dao.insertAll(logs)
    override suspend fun markResolved(id: Long, status: String, notes: String?) = dao.markResolved(id = id, status = status, notes = notes)
    override suspend fun deleteOld(cutoff: Long) = dao.deleteOld(cutoff)
}