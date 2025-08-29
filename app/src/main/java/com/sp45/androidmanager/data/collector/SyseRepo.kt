package com.sp45.androidmanager.data.collector

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.TrafficStats
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.sp45.androidmanager.domain.model.BatteryStatsUi
import kotlinx.coroutines.Dispatchers
import com.sp45.androidmanager.domain.model.CpuStats
import com.sp45.androidmanager.domain.model.SystemStats
import com.sp45.androidmanager.domain.model.MemoryStats
import com.sp45.androidmanager.domain.model.NetworkStatsUi
import com.sp45.androidmanager.domain.model.StorageStatsUi
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

// System information collector
@Singleton
class SystemDataCollector @Inject constructor() {

    companion object {
        private const val TAG = "SystemDataCollector"
    }

    /**
     * Collect all system stats in one call
     */
    suspend fun collectSystemStats(context: Context, hasLocationPermission: Boolean): SystemStats {
        return SystemStats(
            cpu = readCpuStatsSuspend(),
            mem = readMemory(context),
            battery = readBattery(context),
            storage = readStorage(),
            net = readNetwork(context, hasLocationPermission),
            timestamp = System.currentTimeMillis()
        )
    }

    /**
     * Get comprehensive CPU information using multiple metrics
     */
    suspend fun readCpuStatsSuspend(): CpuStats = withContext(Dispatchers.IO) {
        try {
            val systemLoad = readSystemLoad()
            val loadLevel = getLoadLevel(systemLoad)
            val runningProcesses = readRunningProcesses()
            val cpuFreqs = readCpuFreqMHzSuspend()
            val cpuTemp = readCpuTempCSuspend()
            val onlineCores = readOnlineCores()
            val contextSwitches = readContextSwitches()

            CpuStats(
                systemLoad = systemLoad,
                loadLevel = loadLevel,
                runningProcesses = runningProcesses,
                coreFreqMHz = cpuFreqs,
                temperatureC = cpuTemp,
                onlineCores = onlineCores,
                contextSwitches = contextSwitches
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error reading CPU stats: ${e.message}", e)
            // Return default values on error
            CpuStats(
                systemLoad = 0f,
                loadLevel = "UNKNOWN",
                runningProcesses = 0,
                coreFreqMHz = emptyList(),
                temperatureC = null,
                onlineCores = Runtime.getRuntime().availableProcessors(),
                contextSwitches = 0L
            )
        }
    }

    /**
     * Read system load average - MOST RELIABLE method
     * Returns 1-minute load average (0.0 = idle, 1.0 = fully loaded)
     */
    private fun readSystemLoad(): Float {
        return try {
            val loadAvgFile = File("/proc/loadavg")
            if (loadAvgFile.exists() && loadAvgFile.canRead()) {
                val content = loadAvgFile.readText().trim()
                val parts = content.split(" ")
                val load1min = parts.getOrNull(0)?.toFloatOrNull() ?: 0f
                Log.d(TAG, "1-minute load: $load1min")
                load1min
            } else {
                Log.w(TAG, "/proc/loadavg not accessible")
                0f
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading system load: $e")
            0f
        }
    }

    /**
     * Convert load average to human-readable level
     */
    private fun getLoadLevel(load: Float): String {
        val cores = readOnlineCores()
        val loadPerCore = if (cores > 0) load / cores else load

        return when {
            loadPerCore < 0.5f -> "LOW"
            loadPerCore < 0.8f -> "MODERATE"
            loadPerCore < 1.5f -> "HIGH"
            else -> "CRITICAL"
        }
    }

    /**
     * Count running processes from /proc/stat
     */
    private fun readRunningProcesses(): Int {
        return try {
            val statFile = File("/proc/stat")
            if (statFile.exists() && statFile.canRead()) {
                val content = statFile.readText()
                val processesLine = content.lines()
                    .find { it.startsWith("processes ") }

                if (processesLine != null) {
                    val count = processesLine.split(" ")[1].toIntOrNull() ?: 0
                    Log.d(TAG, "Running processes: $count")
                    count
                } else {
                    // Fallback: count /proc directories
                    val procDir = File("/proc")
                    val processCount = procDir.listFiles { file ->
                        file.isDirectory && file.name.matches(Regex("\\d+"))
                    }?.size ?: 0
                    Log.d(TAG, "Process count (fallback): $processCount")
                    processCount
                }
            } else {
                0
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading running processes: $e")
            0
        }
    }

    /**
     * Read context switches - indicates system activity level
     */
    private fun readContextSwitches(): Long {
        return try {
            val statFile = File("/proc/stat")
            if (statFile.exists() && statFile.canRead()) {
                val content = statFile.readText()
                val ctxtLine = content.lines()
                    .find { it.startsWith("ctxt ") }

                val ctxtSwitches = ctxtLine?.split(" ")?.getOrNull(1)?.toLongOrNull() ?: 0L
                Log.d(TAG, "Context switches: $ctxtSwitches")
                ctxtSwitches
            } else {
                0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading context switches: $e")
            0L
        }
    }

    private suspend fun readCpuFreqMHzSuspend(): List<Int> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Int>()
        try {
            val cpuDir = File("/sys/devices/system/cpu")
            val cores = cpuDir.listFiles(FileFilter { it.name.matches(Regex("cpu[0-9]+")) })
                ?.sortedBy { it.name.filter(Char::isDigit).toInt() } ?: emptyList()
            for (core in cores) {
                val f = File(core, "cpufreq/scaling_cur_freq")
                val mhz = try {
                    if (f.exists()) f.readText().trim().toLongOrNull()?.div(1000L)
                        ?.toInt() else null
                } catch (ignored: Exception) {
                    null
                }
                mhz?.let { list += it }
            }
        } catch (e: Exception) {
            Log.w(TAG, "readCpuFreqMHzSuspend failed: $e")
        }
        list
    }

    private suspend fun readCpuTempCSuspend(): Float? = withContext(Dispatchers.IO) {
        try {
            val tz = File("/sys/class/thermal")
            val temps = tz.listFiles { file -> file.name.startsWith("thermal_zone") }
                ?.mapNotNull { z ->
                    val tempF = File(z, "temp")
                    if (tempF.exists()) tempF.readText().trim().toLongOrNull()
                        ?.let { it / 1000f } else null
                } ?: emptyList()
            temps.maxOrNull()
        } catch (e: Exception) {
            Log.w(TAG, "readCpuTempCSuspend failed: $e")
            null
        }
    }

    private fun readOnlineCores(): Int = try {
        Runtime.getRuntime().availableProcessors()
    } catch (_: Exception) {
        0
    }

    fun readMemory(context: Context): MemoryStats {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)

            var cachedMB = 0L
            var swapTotalMB = 0L
            var swapFreeMB = 0L
            try {
                BufferedReader(InputStreamReader(FileInputStream("/proc/meminfo"))).use { br ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        val l = line!!.trim()
                        when {
                            l.startsWith("Cached:") -> cachedMB =
                                l.filter { it.isDigit() }.toLong() / 1024

                            l.startsWith("SwapTotal:") -> swapTotalMB =
                                l.filter { it.isDigit() }.toLong() / 1024

                            l.startsWith("SwapFree:") -> swapFreeMB =
                                l.filter { it.isDigit() }.toLong() / 1024
                        }
                    }
                }
            } catch (_: Exception) {
            }

            val totalMB = mi.totalMem / (1024 * 1024)
            val freeMB = mi.availMem / (1024 * 1024)
            val usedMB = totalMB - freeMB
            val swapUsedMB = (swapTotalMB - swapFreeMB).coerceAtLeast(0)

            MemoryStats(totalMB, usedMB, freeMB, cachedMB, swapTotalMB, swapUsedMB)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading memory stats: ${e.message}", e)
            MemoryStats(0, 0, 0, 0, 0, 0)
        }
    }

    fun readStorage(): StorageStatsUi {
        return try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val total = stat.blockCountLong * stat.blockSizeLong
            val avail = stat.availableBlocksLong * stat.blockSizeLong
            StorageStatsUi(
                internalFreeGB = (avail / (1024f * 1024f * 1024f)),
                internalTotalGB = (total / (1024f * 1024f * 1024f))
            )
        } catch (e: Exception) {
            Log.e(TAG, "readStorage failed: $e")
            StorageStatsUi(0f, 0f)
        }
    }

    fun readBattery(context: Context): BatteryStatsUi {
        return try {
            val i = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = i?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val tempC = try {
                val tPath = File("/sys/class/power_supply/battery/temp")
                if (tPath.exists()) {
                    val raw = tPath.readText().trim().toLongOrNull()
                    raw?.let { if (it > 1000) it / 10f else it / 1000f }
                } else null
            } catch (e: Exception) {
                Log.w(TAG, "readBattery-temp failed: $e")
                null
            }
            val voltageMv = try {
                val vPath = File("/sys/class/power_supply/battery/voltage_now")
                if (vPath.exists()) {
                    vPath.readText().trim().toLongOrNull()?.let { (it / 1000L).toInt() }
                } else {
                    i?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)?.takeIf { it >= 0 }
                }
            } catch (e: Exception) {
                Log.w(TAG, "readBattery-voltage failed: $e")
                null
            }
            val health =
                i?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)?.let { codeToBatteryHealth(it) }
            val status =
                i?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)?.let { codeToBatteryStatus(it) }
            BatteryStatsUi(level, tempC, voltageMv, health, status)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading battery stats: ${e.message}", e)
            BatteryStatsUi(-1, null, null, null, null)
        }
    }

    private fun codeToBatteryHealth(code: Int): String = when (code) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEAT"
        BatteryManager.BATTERY_HEALTH_DEAD -> "DEAD"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER_VOLTAGE"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "FAILURE"
        BatteryManager.BATTERY_HEALTH_COLD -> "COLD"
        else -> "UNKNOWN"
    }

    private fun codeToBatteryStatus(code: Int): String = when (code) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "CHARGING"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "DISCHARGING"
        BatteryManager.BATTERY_STATUS_FULL -> "FULL"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NOT_CHARGING"
        BatteryManager.BATTERY_STATUS_UNKNOWN -> "UNKNOWN"
        else -> "UNKNOWN"
    }

    fun readNetwork(context: Context, haveLocationPermission: Boolean): NetworkStatsUi {
        return try {
            val totalRx = TrafficStats.getTotalRxBytes().coerceAtLeast(0)
            val totalTx = TrafficStats.getTotalTxBytes().coerceAtLeast(0)
            val mobileRx = TrafficStats.getMobileRxBytes().coerceAtLeast(0)
            val mobileTx = TrafficStats.getMobileTxBytes().coerceAtLeast(0)

            val wifiRssi = try {
                val wm =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                if (haveLocationPermission) {
                    @Suppress("DEPRECATION")
                    wm.connectionInfo?.rssi
                } else null
            } catch (e: Exception) {
                Log.w(TAG, "readNetwork wifi rssi failed: $e")
                null
            }

            fun toMB(b: Long) = b / (1024L * 1024L)
            NetworkStatsUi(
                wifiRssiDbm = wifiRssi,
                mobileRxMB = toMB(mobileRx),
                mobileTxMB = toMB(mobileTx),
                totalRxMB = toMB(totalRx),
                totalTxMB = toMB(totalTx)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error reading network stats: ${e.message}", e)
            NetworkStatsUi(null, 0, 0, 0, 0)
        }
    }
}
