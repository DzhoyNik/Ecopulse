package com.example.ecopulse

import android.os.Bundle
import android.widget.Toast
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Настраиваем граф навигации. Стартовая точка — "signin"
                    NavHost(navController = navController, startDestination = "signin") {

                        // Маршрут экрана Входа
                        composable("signin") {
                            SignInScreen(
                                viewModel = authViewModel,
                                onAuthSuccess = {
                                    Toast.makeText(this@MainActivity, "Вход успешен!", Toast.LENGTH_SHORT).show()
                                    // Сюда позже добавим переход на Главный экран трекера
                                },
                                onNavigateToSignUp = {
                                    navController.navigate("signup") // Переключаемся на регистрацию
                                }
                            )
                        }

                        // Маршрут экрана Регистрации
                        composable("signup") {
                            SignUpScreen(
                                viewModel = authViewModel,
                                onAuthSuccess = {
                                    Toast.makeText(this@MainActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                                },
                                onNavigateToSignIn = {
                                    navController.popBackStack() // Возвращаемся назад на Вход
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}