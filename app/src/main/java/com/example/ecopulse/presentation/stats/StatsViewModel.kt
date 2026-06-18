package com.example.ecopulse.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecopulse.domain.usecase.GetEcoTipsUseCase
import com.example.ecopulse.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase, // Используем твой UseCase профиля
    private val getEcoTipsUseCase: GetEcoTipsUseCase         // Используем новый UseCase советов
) : ViewModel() {

    // Получаем количество выполненных целей для аналитики через существующий UseCase
    val completedGoalsCount = getUserProfileUseCase()
        .map { it.completedGoalsCount }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Получаем список советов через новый UseCase
    val ecoTips = getEcoTipsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}