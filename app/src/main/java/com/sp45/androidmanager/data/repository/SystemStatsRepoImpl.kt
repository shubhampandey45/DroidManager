package com.sp45.androidmanager.data.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.sp45.androidmanager.data.collector.SystemDataCollector
import com.sp45.androidmanager.data.database.SystemStatsDao
import com.sp45.androidmanager.data.database.SystemStatsEntity
import com.sp45.androidmanager.data.database.AverageStatsResult
import com.sp45.androidmanager.data.database.toEntity
import com.sp45.androidmanager.domain.model.SystemStats
import com.sp45.androidmanager.domain.repository.SystemStatsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemStatsRepositoryImpl @Inject constructor(
    private val dao: SystemStatsDao,
    private val systemDataCollector: SystemDataCollector,
    @ApplicationContext private val context: Context
) : SystemStatsRepository {

    // Database operations
    override suspend fun insertStats(stats: SystemStatsEntity): Long {
        return dao.insertStats(stats)
    }

    override suspend fun insertStatsList(statsList: List<SystemStatsEntity>) {
        dao.insertStatsList(statsList)
    }

    override fun getAllStatsFlow(): Flow<List<SystemStatsEntity>> {
        return dao.getAllStatsFlow()
    }

    override fun getRecentStatsFlow(limit: Int): Flow<List<SystemStatsEntity>> {
        return dao.getRecentStatsFlow(limit)
    }

    override fun getStatsByTimeRangeFlow(startTime: Long, endTime: Long): Flow<List<SystemStatsEntity>> {
        return dao.getStatsByTimeRangeFlow(startTime, endTime)
    }

    override fun getStatsBySessionFlow(sessionId: String): Flow<List<SystemStatsEntity>> {
        return dao.getStatsBySessionFlow(sessionId)
    }

    override fun getAllSessionsFlow(): Flow<List<String>> {
        return dao.getAllSessionsFlow()
    }

    override fun getLatestStatsFlow(): Flow<SystemStatsEntity?> {
        return dao.getLatestStatsFlow()
    }

    override suspend fun getStatsCount(): Int {
        return dao.getStatsCount()
    }

    override suspend fun getLatestStats(): SystemStatsEntity? {
        return dao.getLatestStats()
    }

    override suspend fun getAverageStats(startTime: Long, endTime: Long): AverageStatsResult? {
        return dao.getAverageStats(startTime, endTime)
    }

    override suspend fun deleteOldStats(cutoffTime: Long): Int {
        return dao.deleteOldStats(cutoffTime)
    }

    override suspend fun deleteSession(sessionId: String): Int {
        return dao.deleteSession(sessionId)
    }

    override suspend fun deleteAllStats(): Int {
        return dao.deleteAllStats()
    }

    // Data collection operations - NEW
    override suspend fun collectCurrentSystemStats(): SystemStats {
        val hasLocationPermission = checkLocationPermission()
        return systemDataCollector.collectSystemStats(context, hasLocationPermission)
    }

    override suspend fun collectAndStoreStats(sessionId: String?): Long {
        val systemStats = collectCurrentSystemStats()
        val entity = systemStats.toEntity(sessionId)
        return insertStats(entity)
    }

    override suspend fun collectCpuStats() = systemDataCollector.readCpuStatsSuspend()

    override fun collectMemoryStats() = systemDataCollector.readMemory(context)

    override fun collectBatteryStats() = systemDataCollector.readBattery(context)

    override fun collectStorageStats() = systemDataCollector.readStorage()

    override fun collectNetworkStats() = systemDataCollector.readNetwork(context, checkLocationPermission())

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}