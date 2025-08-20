package com.sp45.androidmanager.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : NavigationItem("dashboard", "Dashboard", Icons.Default.Home)
    object LiveStats : NavigationItem("livestats", "LiveStats", Icons.Default.Menu)
    object History : NavigationItem("history", "History", Icons.Default.Refresh)
    object Session : NavigationItem("session", "Session", Icons.Default.Build)

}

sealed class DrawerItem(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    // Navigation Drawer
    object Settings : DrawerItem("settings", "Settings", Icons.Default.Settings)
    object ReportBug : DrawerItem("reportbug", "Report Bug", Icons.Default.Email)
    object Suggestions : DrawerItem("suggestion", "Suggestions", Icons.Default.MailOutline)
    object ShareApp : DrawerItem("shareapp", "Share App", Icons.Default.Share)
    object SourceCode : DrawerItem("sourcecode", "Source Code", Icons.Default.Build)

}