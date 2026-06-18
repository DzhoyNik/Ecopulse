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
import com.example.ecopulse.presentation.auth.AuthViewModel
import com.example.ecopulse.presentation.auth.SignInScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Используем стандартную MaterialTheme по умолчанию, без кастомных файлов трекера
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignInScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = {
                            Toast.makeText(this@MainActivity, "Вход успешен!", Toast.LENGTH_SHORT).show()
                        },
                        onNavigateToSignUp = {
                            Toast.makeText(this@MainActivity, "Переход на регистрацию", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}