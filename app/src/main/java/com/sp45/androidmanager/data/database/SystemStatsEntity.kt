package com.sp45.androidmanager.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sp45.androidmanager.domain.model.BatteryStatsUi
import com.sp45.androidmanager.domain.model.CpuStats
import com.sp45.androidmanager.domain.model.MemoryStats
import com.sp45.androidmanager.domain.model.NetworkStatsUi
import com.sp45.androidmanager.domain.model.StorageStatsUi
import com.sp45.androidmanager.domain.model.SystemStats

@Entity(tableName = "system_stats")
data class SystemStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Metadata
    val timestamp: Long,
    val sessionId: String? = null, // For grouping records by monitoring sessions

    // CPU Stats - From your CpuStats model
    val cpuSystemLoad: Float,
    val cpuLoadLevel: String,
    val cpuRunningProcesses: Int,
    val cpuOnlineCores: Int,
    val cpuTemperatureC: Float?,
    val cpuContextSwitches: Long,
    val cpuFreqMHzJson: String, // JSON string for List<Int>

    // Memory Stats - From your MemoryStats model
    val memTotalMB: Long,
    val memUsedMB: Long,
    val memFreeMB: Long,
    val memCachedMB: Long,
    val memSwapTotalMB: Long,
    val memSwapUsedMB: Long,

    // Battery Stats - From your BatteryStatsUi model
    val batteryLevelPct: Int,
    val batteryTemperatureC: Float?,
    val batteryVoltageMV: Int?,
    val batteryHealth: String?,
    val batteryStatus: String?,

    // Storage Stats - From your StorageStatsUi model
    val storageInternalFreeGB: Float,
    val storageInternalTotalGB: Float,

    // Network Stats - From your NetworkStatsUi model
    val networkWifiRssiDbm: Int?,
    val networkMobileRxMB: Long,
    val networkMobileTxMB: Long,
    val networkTotalRxMB: Long,
    val networkTotalTxMB: Long
)

// Extension functions to convert between your existing models and Room entity
fun SystemStats.toEntity(sessionId: String? = null): SystemStatsEntity {
    val gson = Gson()
    return SystemStatsEntity(
        timestamp = System.currentTimeMillis(),
        sessionId = sessionId,

        // CPU mapping
        cpuSystemLoad = cpu.systemLoad,
        cpuLoadLevel = cpu.loadLevel,
        cpuRunningProcesses = cpu.runningProcesses,
        cpuOnlineCores = cpu.onlineCores,
        cpuTemperatureC = cpu.temperatureC,
        cpuContextSwitches = cpu.contextSwitches,
        cpuFreqMHzJson = gson.toJson(cpu.coreFreqMHz),

        // Memory mapping
        memTotalMB = mem.totalMB,
        memUsedMB = mem.usedMB,
        memFreeMB = mem.freeMB,
        memCachedMB = mem.cachedMB,
        memSwapTotalMB = mem.swapTotalMB,
        memSwapUsedMB = mem.swapUsedMB,

        // Battery mapping
        batteryLevelPct = battery.levelPct,
        batteryTemperatureC = battery.temperatureC,
        batteryVoltageMV = battery.voltageMV,
        batteryHealth = battery.health,
        batteryStatus = battery.status,

        // Storage mapping
        storageInternalFreeGB = storage.internalFreeGB,
        storageInternalTotalGB = storage.internalTotalGB,

        // Network mapping
        networkWifiRssiDbm = net.wifiRssiDbm,
        networkMobileRxMB = net.mobileRxMB,
        networkMobileTxMB = net.mobileTxMB,
        networkTotalRxMB = net.totalRxMB,
        networkTotalTxMB = net.totalTxMB
    )
}

fun SystemStatsEntity.toDomainModel(): SystemStats {
    val gson = Gson()
    val listType = object : TypeToken<List<Int>>() {}.type
    val cpuFreqMHz: List<Int> = try {
        gson.fromJson(cpuFreqMHzJson, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    return SystemStats(
        timestamp = timestamp,
        cpu = CpuStats(
            systemLoad = cpuSystemLoad,
            loadLevel = cpuLoadLevel,
            runningProcesses = cpuRunningProcesses,
            coreFreqMHz = cpuFreqMHz,
            temperatureC = cpuTemperatureC,
            onlineCores = cpuOnlineCores,
            contextSwitches = cpuContextSwitches
        ),
        mem = MemoryStats(
            totalMB = memTotalMB,
            usedMB = memUsedMB,
            freeMB = memFreeMB,
            cachedMB = memCachedMB,
            swapTotalMB = memSwapTotalMB,
            swapUsedMB = memSwapUsedMB
        ),
        battery = BatteryStatsUi(
            levelPct = batteryLevelPct,
            temperatureC = batteryTemperatureC,
            voltageMV = batteryVoltageMV,
            health = batteryHealth,
            status = batteryStatus
        ),
        storage = StorageStatsUi(
            internalFreeGB = storageInternalFreeGB,
            internalTotalGB = storageInternalTotalGB
        ),
        net = NetworkStatsUi(
            wifiRssiDbm = networkWifiRssiDbm,
            mobileRxMB = networkMobileRxMB,
            mobileTxMB = networkMobileTxMB,
            totalRxMB = networkTotalRxMB,
            totalTxMB = networkTotalTxMB
        )
    )
}
