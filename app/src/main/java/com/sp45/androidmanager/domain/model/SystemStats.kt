package com.sp45.androidmanager.domain.model

data class SystemStats(
    val timestamp: Long,
    val cpu: CpuStats,
    val mem: MemoryStats,
    val battery: BatteryStatsUi,
    val storage: StorageStatsUi,
    val net: NetworkStatsUi
)

data class MemoryStats(
    val totalMB: Long,
    val usedMB: Long,
    val freeMB: Long,
    val cachedMB: Long,
    val swapTotalMB: Long,
    val swapUsedMB: Long,
)

/**
 * Updated CPU stats with multiple metrics instead of just percentage
 */
data class CpuStats(
    val systemLoad: Float,           // Load average (0.0 to 4.0+)
    val loadLevel: String,           // LOW, MODERATE, HIGH, CRITICAL
    val runningProcesses: Int,       // Number of running processes
    val coreFreqMHz: List<Int>,      // CPU frequencies
    val temperatureC: Float?,        // CPU temperature
    val onlineCores: Int,            // Available cores
    val contextSwitches: Long        // Context switches (system activity)
)

data class BatteryStatsUi(
    val levelPct: Int,
    val temperatureC: Float?,
    val voltageMV: Int?,
    val health: String?,
    val status: String?
)

data class StorageStatsUi(
    val internalFreeGB: Float,
    val internalTotalGB: Float
)

data class NetworkStatsUi(
    val wifiRssiDbm: Int?,
    val mobileRxMB: Long,
    val mobileTxMB: Long,
    val totalRxMB: Long,
    val totalTxMB: Long
)