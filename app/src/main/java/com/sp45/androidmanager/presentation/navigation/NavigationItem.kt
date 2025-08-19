package com.sp45.androidmanager.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Dashboard : NavigationItem("dashboard", "Dashboard", Icons.Default.Home)
    object LiveStats : NavigationItem("livestats", "LiveStats", Icons.Default.Menu)
    object History: NavigationItem("history", "History", Icons.Default.Refresh)
    object Session: NavigationItem("session", "Session", Icons.Default.Build)
}