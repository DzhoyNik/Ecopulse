package com.example.ecopulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ecopulse.presentation.auth.AuthViewModel
import com.example.ecopulse.presentation.auth.SignInScreen
import com.example.ecopulse.presentation.auth.SignUpScreen
import com.example.ecopulse.presentation.goals.GoalsScreen
import com.example.ecopulse.presentation.goals.GoalsViewModel
import com.example.ecopulse.presentation.map.EcoMapScreen
import com.example.ecopulse.presentation.map.EcoMapViewModel
import com.example.ecopulse.presentation.profile.ProfileScreen
import com.example.ecopulse.presentation.profile.ProfileViewModel
import com.example.ecopulse.presentation.stats.StatsScreen
import com.example.ecopulse.presentation.stats.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ecopulse.data.worker.EcoSyncWorker
import com.example.ecopulse.presentation.stats.AiAdvisorViewModel
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val goalsViewModel: GoalsViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val statsViewModel: StatsViewModel by viewModels()
    private val aiAdvisorViewModel: AiAdvisorViewModel by viewModels()
    private val mapViewModel: EcoMapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Скрываем нижнее меню на экранах авторизации и регистрации
                val showBottomBar = currentRoute in listOf("goals", "profile", "stats", "map")

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == "goals",
                                    onClick = { navController.navigate("goals") { launchSingleTop = true } },
                                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Цели") },
                                    label = { Text("Цели") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "stats",
                                    onClick = { navController.navigate("stats") { launchSingleTop = true } },
                                    icon = { Icon(Icons.Default.Info, contentDescription = "Инфо") },
                                    label = { Text("Аналитика") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "map",
                                    onClick = { navController.navigate("map") { launchSingleTop = true } },
                                    icon = { Icon(Icons.Default.Map, contentDescription = "Карта") },
                                    label = { Text("Карта") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "profile",
                                    onClick = { navController.navigate("profile") { launchSingleTop = true } },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                                    label = { Text("Профиль") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = "signin") {
                            composable("signin") {
                                SignInScreen(
                                    viewModel = authViewModel,
                                    onAuthSuccess = {
                                        navController.navigate("goals") {
                                            popUpTo("signin") { inclusive = true }
                                        }
                                    },
                                    onNavigateToSignUp = { navController.navigate("signup") }
                                )
                            }

                            composable("signup") {
                                SignUpScreen(
                                    viewModel = authViewModel,
                                    onAuthSuccess = {
                                        navController.navigate("goals") {
                                            popUpTo("signup") { inclusive = true }
                                        }
                                    },
                                    onNavigateToSignIn = { navController.popBackStack() }
                                )
                            }

                            composable("goals") {
                                GoalsScreen(
                                    viewModel = goalsViewModel
                                )
                            }

                            composable("profile") {
                                ProfileScreen(
                                    viewModel = profileViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            // НАШ 5-й ЭКРАН СТАТИСТИКИ И ИНФО
                            composable("stats") {
                                StatsScreen(viewModel = statsViewModel, aiViewModel = aiAdvisorViewModel)
                            }

                            composable("map") {
                                EcoMapScreen(viewModel = mapViewModel)
                            }
                        }
                    }
                }
            }
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Только при наличии интернета
            .setRequiresCharging(true) // Только на зарядке (экономим батарею)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<EcoSyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "eco_sync",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}