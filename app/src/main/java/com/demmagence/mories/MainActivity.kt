package com.demmagence.mories

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.demmagence.mories.ui.navigation.BottomNavItem
import com.demmagence.mories.ui.navigation.NavGraph
import com.demmagence.mories.ui.navigation.Screen
import com.demmagence.mories.ui.theme.MoriesBackground
import com.demmagence.mories.ui.theme.MoriesPrimary
import com.demmagence.mories.ui.theme.MoriesSurface
import com.demmagence.mories.ui.theme.MoriesTextSecondary
import com.demmagence.mories.ui.theme.MoriesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen FIRST (before super.onCreate as required by SplashScreen API)
        installSplashScreen()

        // ALWAYS call super.onCreate() - this is REQUIRED for the Activity to function
        super.onCreate(savedInstanceState)

        // Check for previous crash log
        val crashFile = java.io.File(cacheDir, "crash_log.txt")
        if (crashFile.exists()) {
            val stackTrace = try {
                crashFile.readText()
            } catch (e: Exception) {
                "Failed to read crash log: ${e.message}"
            }
            // Delete after reading
            try { crashFile.delete() } catch (_: Exception) {}

            try { enableEdgeToEdge() } catch (_: Exception) {}
            showCrashScreen(stackTrace)
            return
        }

        try {
            enableEdgeToEdge()
            setContent {
                MoriesTheme {
                    MoriesMainContent()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            showCrashScreen(e.stackTraceToString())
        }
    }

    private fun showCrashScreen(errorText: String) {
        setContent {
            androidx.compose.material3.MaterialTheme {
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color(0xFF0F0F0F))
                        .padding(24.dp),
                    contentAlignment = androidx.compose.ui.Alignment.TopStart
                ) {
                    androidx.compose.foundation.layout.Column(
                        modifier = androidx.compose.ui.Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        androidx.compose.material3.Text(
                            text = "Mories - Terjadi Kesalahan",
                            color = androidx.compose.ui.graphics.Color(0xFFE50914),
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                        androidx.compose.material3.Text(
                            text = "Silakan screenshot layar ini untuk melaporkan error:",
                            color = androidx.compose.ui.graphics.Color.White,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                        androidx.compose.material3.Card(
                            colors = androidx.compose.material3.CardDefaults.cardColors(
                                containerColor = androidx.compose.ui.graphics.Color(0xFF1F1F1F)
                            ),
                            modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                        ) {
                            androidx.compose.material3.Text(
                                text = errorText,
                                color = androidx.compose.ui.graphics.Color(0xFFCCCCCC),
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                modifier = androidx.compose.ui.Modifier.padding(12.dp),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoriesMainContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if bottom bar should be shown
    val showBottomBar = currentDestination?.let { dest ->
        BottomNavItem.entries.any { item ->
            dest.hasRoute(item.route::class)
        }
    } ?: true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MoriesBackground,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MoriesSurface,
                    contentColor = Color.White
                ) {
                    BottomNavItem.entries.forEach { item ->
                        val isSelected = currentDestination?.hasRoute(item.route::class) == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MoriesPrimary,
                                selectedTextColor = MoriesPrimary,
                                unselectedIconColor = MoriesTextSecondary,
                                unselectedTextColor = MoriesTextSecondary,
                                indicatorColor = MoriesPrimary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
