package com.sp45.androidmanager.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SystemStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStats(stats: SystemStatsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatsList(statsList: List<SystemStatsEntity>)

    @Query("SELECT * FROM system_stats ORDER BY timestamp DESC")
    fun getAllStatsFlow(): Flow<List<SystemStatsEntity>>

    @Query("SELECT * FROM system_stats ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentStatsFlow(limit: Int): Flow<List<SystemStatsEntity>>

    @Query("""
        SELECT * FROM system_stats 
        WHERE timestamp >= :startTime AND timestamp <= :endTime 
        ORDER BY timestamp DESC
    """)
    fun getStatsByTimeRangeFlow(startTime: Long, endTime: Long): Flow<List<SystemStatsEntity>>

    @Query("SELECT * FROM system_stats WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    fun getStatsBySessionFlow(sessionId: String): Flow<List<SystemStatsEntity>>

    @Query("SELECT DISTINCT sessionId FROM system_stats WHERE sessionId IS NOT NULL ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM system_stats")
    suspend fun getStatsCount(): Int

    @Query("SELECT * FROM system_stats ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestStats(): SystemStatsEntity?

    @Query("SELECT * FROM system_stats ORDER BY timestamp DESC LIMIT 1")
    fun getLatestStatsFlow(): Flow<SystemStatsEntity?>

    @Query("DELETE FROM system_stats WHERE timestamp < :cutoffTime")
    suspend fun deleteOldStats(cutoffTime: Long): Int

    @Query("DELETE FROM system_stats WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String): Int

    @Query("DELETE FROM system_stats")
    suspend fun deleteAllStats(): Int

    // Useful aggregation queries for analytics
    @Query("""
        SELECT AVG(cpuSystemLoad) as avgCpuLoad, 
               AVG(memUsedMB) as avgMemUsed,
               AVG(batteryLevelPct) as avgBatteryLevel
        FROM system_stats 
        WHERE timestamp >= :startTime AND timestamp <= :endTime
    """)
    suspend fun getAverageStats(startTime: Long, endTime: Long): AverageStatsResult?
}

// Data class for aggregation results
data class AverageStatsResult(
    val avgCpuLoad: Double,
    val avgMemUsed: Double,
    val avgBatteryLevel: Double
)