package com.sp45.androidmanager.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sp45.androidmanager.presentation.ui.history.HistoryScreen
import com.sp45.androidmanager.presentation.ui.livestats.LiveStatsScreen
import com.sp45.androidmanager.presentation.ui.main.Dashboard
import com.sp45.androidmanager.presentation.ui.main.StatsViewModel
import com.sp45.androidmanager.presentation.ui.session.SessionScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: StatsViewModel) {
    // For bottom navigation
    val navController = rememberNavController()
    val items = listOf(
        NavigationItem.Dashboard,
        NavigationItem.LiveStats,
        NavigationItem.Session,
        NavigationItem.History
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // For top app bar
    val topBarTitle = when (currentDestination?.route) {
        NavigationItem.LiveStats.route -> "Live Analytics"
        NavigationItem.History.route -> "History"
        NavigationItem.Session.route -> "Sessions"
        else -> "Droid Manager"
    }

//    val baseGreen = Color(0xFF87B644)
//    val lightVariant = lerp(baseGreen, Color.White, 0.35f)
//    val isDark = isSystemInDarkTheme()
//    val tabColor = if (isDark) baseGreen else lightVariant

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Dashboard.route) {
                Dashboard(viewModel = viewModel)
            }
            composable(NavigationItem.LiveStats.route) {
                LiveStatsScreen(viewModel)
            }
            composable(NavigationItem.Session.route) {
                SessionScreen(modifier = Modifier, viewModel)
            }
            composable(NavigationItem.History.route) {
                HistoryScreen(viewModel)
            }
        }
    }
}