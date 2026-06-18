package com.example.ecopulse.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun signIn(email: String, pass: String) {
        // Простая валидация на заполненность полей
        if (email.isBlank() || pass.isBlank()) {
            _state.update { it.copy(errorMessage = "Заполните все поля") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            delay(1500) // Имитируем сетевой запрос к серверу

            // Предположим, вход всегда успешный для теста
            _state.update { it.copy(isLoading = false, isSuccess = true) }
        }
    }

    fun resetState() {
        _state.value = AuthState()
    }
}