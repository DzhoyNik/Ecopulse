package com.example.ecopulse.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecopulse.BuildConfig
import com.example.ecopulse.domain.usecase.GetUserProfileUseCase
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AiAdviceState {
    object Idle : AiAdviceState()
    object Loading : AiAdviceState()
    data class Success(val advice: String) : AiAdviceState()
    data class Error(val message: String) : AiAdviceState()
}

@HiltViewModel
class AiAdvisorViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AiAdviceState>(AiAdviceState.Idle)
    val state = _state.asStateFlow()
    private val apiKey: String = BuildConfig.API_KEY

    val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    fun getPersonalAdvice() {
        viewModelScope.launch {
            _state.value = AiAdviceState.Loading
            try {
                val profile = getUserProfileUseCase().first()

                val prompt = """
                    Ты — экологический советник в приложении EcoPulse.
                    
                    Данные пользователя:
                    - Имя: ${profile.name}
                    - Выполнено эко-целей: ${profile.completedGoalsCount}
                    - Эко-очки: ${profile.ecoPoints}
                    - Прогресс уровня: ${(profile.levelProgress * 100).toInt()}%
                    
                    На основе этих данных дай короткий (3-4 предложения) персональный совет:
                    что пользователь делает хорошо и какую конкретную эко-привычку стоит добавить следующей.
                    Отвечай на русском языке, тепло и мотивирующе.
                """.trimIndent()

                val response = model.generateContent(prompt)
                _state.value = AiAdviceState.Success(
                    response.text ?: "Не удалось получить совет"
                )
            } catch (e: Exception) {
                _state.value = AiAdviceState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun reset() {
        _state.value = AiAdviceState.Idle
    }
}