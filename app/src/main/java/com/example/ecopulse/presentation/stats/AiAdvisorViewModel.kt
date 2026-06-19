package com.example.ecopulse.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecopulse.BuildConfig
import com.example.ecopulse.domain.usecase.GetUserProfileUseCase
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
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

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val model = GenerativeModel(
        // 1.5-flash отключён для новых проектов (404). Актуальная модель.
        // Точный Model API ID для вашего ключа можно проверить в Google AI Studio.
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    fun getPersonalAdvice() {
        // Без ключа модель «висит» — сразу даём понятную ошибку
        if (apiKey.isBlank()) {
            _state.value = AiAdviceState.Error("Ключ Gemini не задан. Добавьте GEMINI_API_KEY в local.properties.")
            return
        }
        viewModelScope.launch {
            _state.value = AiAdviceState.Loading
            try {
                // Жёсткий таймаут, чтобы экран не зависал бесконечно
                val advice = withTimeout(30_000L) {
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
                Firebase.crashlytics.log("ai_advice_success: points=${profile.ecoPoints} goals=${profile.completedGoalsCount}")
                response.text ?: "Не удалось получить совет"
                }
                _state.value = AiAdviceState.Success(advice)
            } catch (e: TimeoutCancellationException) {
                Firebase.crashlytics.recordException(e)
                _state.value = AiAdviceState.Error("Превышено время ожидания ответа ИИ")
            } catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
                _state.value = AiAdviceState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun reset() {
        _state.value = AiAdviceState.Idle
    }
}