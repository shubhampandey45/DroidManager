package com.sp45.androidmanager.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//// Helper functions for formatting and colors
//fun formatTimestamp(timestamp: Long): String {
//    val sdf = java.text.SimpleDateFormat("MMM dd, HH:mm:ss", java.util.Locale.getDefault())
//    return sdf.format(java.util.Date(timestamp))
//}

fun formatMB(mb: Long): String {
    return when {
        mb >= 1024 -> "${String.format("%.1f", mb / 1024.0)}GB"
        else -> "${mb}MB"
    }
}

@Composable
fun getCpuLoadColor(loadLevel: String): Color {
    return when (loadLevel) {
        "LOW" -> Color(0xFF4CAF50)        // Green
        "MODERATE" -> Color(0xFFFF9800)   // Orange
        "HIGH" -> Color(0xFFFF5722)       // Red-Orange
        "CRITICAL" -> Color(0xFFD32F2F)   // Red
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
fun getBatteryColor(level: Int): Color {
    return when {
        level >= 60 -> Color(0xFF4CAF50)   // Green
        level >= 30 -> Color(0xFFFF9800)   // Orange
        level >= 15 -> Color(0xFFFF5722)   // Red-Orange
        else -> Color(0xFFD32F2F)          // Red
    }
}

@Composable
fun getWifiSignalColor(rssi: Int): Color {
    return when {
        rssi >= -50 -> Color(0xFF4CAF50)   // Excellent
        rssi >= -60 -> Color(0xFF8BC34A)   // Good
        rssi >= -70 -> Color(0xFFFF9800)   // Fair
        else -> Color(0xFFFF5722)          // Poor
    }
}


//private fun formatMB(mb: Long): String {
//    return when {
//        mb >= 1024 -> String.format("%.1f GB", mb / 1024f)
//        else -> "${mb} MB"
//    }
//}

//private fun getCpuLoadColor(loadLevel: String): Color {
//    return when (loadLevel) {
//        "LOW" -> Color(0xFF4CAF50)        // Green
//        "MODERATE" -> Color(0xFFFF9800)   // Orange
//        "HIGH" -> Color(0xFFFF5722)       // Red-Orange
//        "CRITICAL" -> Color(0xFFD32F2F)   // Red
//        else -> Color.Gray
//    }
//}

//private fun getBatteryColor(levelPct: Int): Color {
//    return when {
//        levelPct >= 60 -> Color(0xFF4CAF50)     // Green
//        levelPct >= 30 -> Color(0xFFFF9800)     // Orange
//        levelPct >= 15 -> Color(0xFFFF5722)     // Red-Orange
//        else -> Color(0xFFD32F2F)                // Red
//    }
//}
//
//private fun getWifiSignalColor(rssiDbm: Int): Color {
//    return when {
//        rssiDbm >= -50 -> Color(0xFF4CAF50)     // Excellent
//        rssiDbm >= -60 -> Color(0xFF8BC34A)     // Good
//        rssiDbm >= -70 -> Color(0xFFFF9800)     // Fair
//        else -> Color(0xFFD32F2F)                // Poor
//    }
//}