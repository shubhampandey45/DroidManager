package com.sp45.androidmanager.presentation.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sp45.androidmanager.R
import com.sp45.androidmanager.presentation.ui.history.HistoryScreen
import com.sp45.androidmanager.presentation.ui.livestats.LiveStatsScreen
import com.sp45.androidmanager.presentation.ui.main.Dashboard
import com.sp45.androidmanager.presentation.ui.main.StatsViewModel
import com.sp45.androidmanager.presentation.ui.session.SessionScreen
import com.sp45.androidmanager.presentation.ui.settings.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

    // Navigation Drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(drawerState, scope, navController)
        }
    ) {
        Scaffold(
            topBar = {
                if (currentDestination?.route != DrawerItem.Settings.route) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = topBarTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black,
                            titleContentColor = Color.White
                        )
                    )
                }
            },
            bottomBar = {
                if (currentDestination?.route != DrawerItem.Settings.route) {
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
//
//                // Drawer destinations
//                composable(DrawerItem.Settings.route) {
//                    SettingsScreen(navController = navController)
//                }
            }
        }
    }
}


@Composable
fun NavigationDrawerContent(
    drawerState: DrawerState,
    scope: CoroutineScope,
    navController: NavHostController
) {
//    val drawerItemsTop = listOf(
//        DrawerItem.Settings
//    )

    val drawerItemsBottom = listOf(
        DrawerItem.ReportBug,
        DrawerItem.Suggestions,
        DrawerItem.ShareApp,
        DrawerItem.SourceCode,
    )

    ModalDrawerSheet(
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Header Section
            Spacer(Modifier.height(80.dp))
            Image(
                painter = painterResource(R.drawable.icon), // your app logo
                contentDescription = "App logo",
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Droid Manager",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "v0",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp)

            // 🔹 Top group items
//            drawerItemsTop.forEachIndexed { index, item ->
//                NavigationDrawerItem(
//                    icon = { Icon(item.icon, contentDescription = item.title) },
//                    label = { Text(item.title) },
//                    selected = false,
//                    onClick = {
//                        scope.launch { drawerState.close() }
//                        navController.navigate(item.route)
//                    },
//                    modifier = Modifier
//                        .padding(NavigationDrawerItemDefaults.ItemPadding),
//                    colors = NavigationDrawerItemDefaults.colors(
//                        selectedContainerColor = Color.Transparent, // 🔹 remove background
//                        unselectedContainerColor = Color.Transparent,
//                        selectedIconColor = Color.White,
//                        unselectedIconColor = Color.White,
//                        selectedTextColor = Color.White,
//                        unselectedTextColor = Color.White
//                    )
//                )
//            }

          //  HorizontalDivider(thickness = 1.dp)

            // 🔹 Bottom group items
            drawerItemsBottom.forEachIndexed { index, item ->
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        when (item) {
                            DrawerItem.ReportBug -> bugReportEmail(navController.context)
                            DrawerItem.Suggestions -> suggestionsEmail(navController.context)
                            DrawerItem.ShareApp -> shareApp(navController.context)
                            DrawerItem.SourceCode -> shareSourceCode(navController.context)
                            else -> {}
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color.Transparent, // 🔹 remove background
                        unselectedContainerColor = Color.Transparent,
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White
                    )
                )
            }
        }
    }
}

private fun bugReportEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("shubhampandey5410@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Bug Report - Droid Manager")
        putExtra(Intent.EXTRA_TEXT, "Describe the issue here...")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

private fun suggestionsEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("shubhampandey5410@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Suggestion - Droid Manager")
        putExtra(Intent.EXTRA_TEXT, "Describe the suggestion here...")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

private fun shareApp(context: Context) {
    val shareText =
        "https://drive.google.com/drive/folders/1bzlNXCM_JkP70SPpzFM5wzVw-87atg2B?usp=sharing"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out Droid Manager!")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(
        Intent.createChooser(intent, "Share Driod Manager via")
    )
}

private fun shareSourceCode(context: Context) {
    val githubUrl = "https://github.com/shubhampandey45/DroidManager"
    val intent = Intent(Intent.ACTION_VIEW, githubUrl.toUri()).apply {
        setPackage("com.github.android")
    }

    val chooseIntent = if (intent.resolveActivity(context.packageManager) != null) {
        intent
    } else {
        Intent(Intent.ACTION_VIEW, githubUrl.toUri())
    }
    context.startActivity(chooseIntent)
}