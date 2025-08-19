package com.sp45.androidmanager.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import com.sp45.androidmanager.data.service.SystemMonitoringService

object ServiceUtils {

    fun startMonitoringService(context: Context) {
        val intent = Intent(context, SystemMonitoringService::class.java).apply {
            action = SystemMonitoringService.ACTION_START_MONITORING
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        }
    }

    fun stopMonitoringService(context: Context) {
        val intent = Intent(context, SystemMonitoringService::class.java).apply {
            action = SystemMonitoringService.ACTION_STOP_MONITORING
        }
        context.startService(intent)
    }

    fun isServiceRunning(context: Context): Boolean {
        // You can implement service status checking here if needed
        // For now, we'll rely on ViewModel state management
        return false
    }
}