package com.example.morningfocusalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.morningfocusalarm.ui.screens.applist.AppListScreen
import com.example.morningfocusalarm.ui.screens.dashboard.DashboardScreen
import com.example.morningfocusalarm.ui.screens.onboarding.OnboardingScreen
import com.example.morningfocusalarm.ui.screens.settings.SettingsScreen
import com.example.morningfocusalarm.ui.theme.MorningFocusAlarmTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MorningFocusAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: MainViewModel = hiltViewModel()
                    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()
                    
                    NavHost(
                        navController = navController,
                        startDestination = if (isOnboardingCompleted) "dashboard" else "onboarding"
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(
                                onComplete = {
                                    navController.navigate("dashboard") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToAppList = { navController.navigate("applist") }
                            )
                        }
                        composable("applist") {
                            AppListScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}