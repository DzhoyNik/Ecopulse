package com.example.ecopulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecopulse.presentation.auth.AuthViewModel
import com.example.ecopulse.presentation.auth.SignInScreen
import com.example.ecopulse.presentation.auth.SignUpScreen
import com.example.ecopulse.presentation.goals.GoalsScreen
import com.example.ecopulse.presentation.goals.GoalsViewModel
import com.example.ecopulse.presentation.profile.ProfileScreen
import com.example.ecopulse.presentation.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val goalsViewModel: GoalsViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels() // Добавили

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

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
                                viewModel = goalsViewModel,
                                onNavigateToProfile = {
                                    navController.navigate("profile") // Переходим в профиль
                                }
                            )
                        }

                        // МАРШРУТ ГИБРИДНОГО ЭКРАНА (XML + Compose)
                        composable("profile") {
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}