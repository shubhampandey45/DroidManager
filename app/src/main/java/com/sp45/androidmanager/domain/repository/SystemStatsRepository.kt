package com.sp45.androidmanager.domain.repository

import com.sp45.androidmanager.data.database.SystemStatsEntity
import com.sp45.androidmanager.data.database.AverageStatsResult
import com.sp45.androidmanager.domain.model.*
import kotlinx.coroutines.flow.Flow

interface SystemStatsRepository {

    // Database operations
    suspend fun insertStats(stats: SystemStatsEntity): Long
    suspend fun insertStatsList(statsList: List<SystemStatsEntity>)

    fun getAllStatsFlow(): Flow<List<SystemStatsEntity>>
    fun getRecentStatsFlow(limit: Int): Flow<List<SystemStatsEntity>>
    fun getStatsByTimeRangeFlow(startTime: Long, endTime: Long): Flow<List<SystemStatsEntity>>
    fun getStatsBySessionFlow(sessionId: String): Flow<List<SystemStatsEntity>>
    fun getAllSessionsFlow(): Flow<List<String>>
    fun getLatestStatsFlow(): Flow<SystemStatsEntity?>

    suspend fun getStatsCount(): Int
    suspend fun getLatestStats(): SystemStatsEntity?
    suspend fun getAverageStats(startTime: Long, endTime: Long): AverageStatsResult?

    suspend fun deleteOldStats(cutoffTime: Long): Int
    suspend fun deleteSession(sessionId: String): Int
    suspend fun deleteAllStats(): Int

    // Data collection operations - NEW
    suspend fun collectCurrentSystemStats(): SystemStats
    suspend fun collectAndStoreStats(sessionId: String? = null): Long

    // Individual collectors
    suspend fun collectCpuStats(): CpuStats
    fun collectMemoryStats(): MemoryStats
    fun collectBatteryStats(): BatteryStatsUi
    fun collectStorageStats(): StorageStatsUi
    fun collectNetworkStats(): NetworkStatsUi
}