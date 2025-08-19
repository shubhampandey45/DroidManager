package com.sp45.androidmanager.presentation.ui.main

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sp45.androidmanager.data.database.toDomainModel
import com.sp45.androidmanager.data.service.SystemMonitoringService
import com.sp45.androidmanager.domain.model.SystemStats
import com.sp45.androidmanager.domain.repository.SystemStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: SystemStatsRepository,
    application: Application
) : AndroidViewModel(application) {

    // Real-time stats (for live display)
    private val _liveStats = mutableStateOf<SystemStats?>(null)
    val liveStats get() = _liveStats

    // Rolling buffer for last N live samples (for sparklines)
    private val _recentLiveSamples = MutableStateFlow<List<SystemStats>>(emptyList())
    val recentLiveSamples: StateFlow<List<SystemStats>> = _recentLiveSamples.asStateFlow()
    private val recentBufferSize = 40 // keep last 40 samples (~40 seconds at 1s interval)

    // Service state
    private val _serviceRunning = MutableStateFlow(false)
    val serviceRunning: StateFlow<Boolean> = _serviceRunning.asStateFlow()

    // Historical data from database - Updated to ensure fresh data
    val recentStoredStats = repository.getRecentStatsFlow(100)
        .map { entities -> entities.map { it.toDomainModel() } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Latest stored stat
    val latestStoredStat = repository.getLatestStatsFlow()
        .map { entity -> entity?.toDomainModel() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    // Database info
    private val _databaseInfo = MutableStateFlow(DatabaseInfo())
    val databaseInfo: StateFlow<DatabaseInfo> = _databaseInfo.asStateFlow()

    init {
        // Load initial database info
        viewModelScope.launch {
            updateDatabaseInfo()
        }
    }

    private var liveUpdatesJob: Job? = null

    /**
     * Start real-time stats updates (for live display - 1 second intervals)
     * This is separate from the background service
     */
    fun startLiveUpdates() {
        // Prevent starting multiple jobs
        if (liveUpdatesJob?.isActive == true) return

        liveUpdatesJob = viewModelScope.launch {
            // immediate initial fetch so UI shows something right away
            try {
                val initial = repository.collectCurrentSystemStats()
                _liveStats.value = initial
                pushToRecent(initial)
            } catch (e: Exception) {
                // optional: log
            }

            while (isActive) {
                try {
                    delay(1000L)
                    val stats = repository.collectCurrentSystemStats()
                    _liveStats.value = stats
                    pushToRecent(stats)
                } catch (e: Exception) {
                    // log or update error UI if needed
                }
            }
        }
    }

    fun stopLiveUpdates() {
        liveUpdatesJob?.cancel()
        liveUpdatesJob = null
        _liveStats.value = null
        // keep recent buffer (useful). If you want to clear buffer too, uncomment:
        // _recentLiveSamples.value = emptyList()
    }

    private fun pushToRecent(stats: SystemStats) {
        val cur = _recentLiveSamples.value.toMutableList()
        cur.add(stats)
        if (cur.size > recentBufferSize) cur.removeAt(0)
        _recentLiveSamples.value = cur
    }

    /**
     * Start the background monitoring service (30-second intervals to database)
     */
    fun startMonitoringService() {
        val intent = Intent(getApplication(), SystemMonitoringService::class.java).apply {
            action = SystemMonitoringService.ACTION_START_MONITORING
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplication<Application>().startForegroundService(intent)
        } else {
            getApplication<Application>().startService(intent)
        }
        _serviceRunning.value = true

        // Also start live updates for immediate UI feedback
        startLiveUpdates()
    }

    /**
     * Stop the background monitoring service
     */
    fun stopMonitoringService() {
        val intent = Intent(getApplication(), SystemMonitoringService::class.java).apply {
            action = SystemMonitoringService.ACTION_STOP_MONITORING
        }
        getApplication<Application>().startService(intent)
        _serviceRunning.value = false

        // Stop live updates as well
        stopLiveUpdates()
    }

    /**
     * Manual data collection and storage
     */
    fun collectDataManually() {
        viewModelScope.launch {
            try {
                val id = repository.collectAndStoreStats("manual_${System.currentTimeMillis()}")
                updateDatabaseInfo()
                // Update live stats too
                val stats = repository.collectCurrentSystemStats()
                _liveStats.value = stats
                pushToRecent(stats)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Refresh database info without collecting new data
     * This just updates the count and size information
     */
    fun refreshDatabaseInfo() {
        viewModelScope.launch {
            updateDatabaseInfo()
        }
    }

    /**
     * Clear all stored data
     */
    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllStats()
            updateDatabaseInfo()
        }
    }

    /**
     * Delete old data (older than specified days)
     */
    fun deleteOldData(daysOld: Int) {
        viewModelScope.launch {
            val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
            val deletedCount = repository.deleteOldStats(cutoffTime)
            updateDatabaseInfo()
        }
    }

    private suspend fun updateDatabaseInfo() {
        try {
            val count = repository.getStatsCount()
            val latest = repository.getLatestStats()
            _databaseInfo.value = DatabaseInfo(
                totalRecords = count,
                latestTimestamp = latest?.timestamp,
                databaseSizeEstimate = count * 0.5f // Rough estimate in KB
            )
        } catch (e: Exception) {
            _databaseInfo.value = DatabaseInfo(
                totalRecords = 0,
                latestTimestamp = null,
                databaseSizeEstimate = 0f,
                error = e.message
            )
        }
    }

    /**
     * Get stats for a specific time range
     */
    fun getStatsForTimeRange(startTime: Long, endTime: Long) =
        repository.getStatsByTimeRangeFlow(startTime, endTime)
            .map { entities -> entities.map { it.toDomainModel() } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    /**
     * Get all monitoring sessions
     */
    val allSessions = repository.getAllSessionsFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    /**
     * Get stats for a specific session
     */
    fun getStatsForSession(sessionId: String) =
        repository.getStatsBySessionFlow(sessionId)
            .map { entities -> entities.map { it.toDomainModel() } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            updateDatabaseInfo()
        }
    }
}

/**
 * Data class to hold database information
 */
data class DatabaseInfo(
    val totalRecords: Int = 0,
    val latestTimestamp: Long? = null,
    val databaseSizeEstimate: Float = 0f, // in KB
    val error: String? = null
)