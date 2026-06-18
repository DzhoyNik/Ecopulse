package com.example.ecopulse.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecopulse.domain.model.EcoGoal
import com.example.ecopulse.domain.usecase.GetEcoGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    getEcoGoalsUseCase: GetEcoGoalsUseCase
) : ViewModel() {

    private val _goalsState = MutableStateFlow<List<EcoGoal>>(emptyList())
    val goalsState = _goalsState.asStateFlow()

    init {
        // Загружаем цели сразу при создании ViewModel
        getEcoGoalsUseCase()
            .onEach { list -> _goalsState.value = list }
            .launchIn(viewModelScope)
    }

    fun completeGoal(goalId: String) {
        // Просто меняем статус цели в локальном списке для демонстрации работы
        _goalsState.update { currentList ->
            currentList.map { goal ->
                if (goal.id == goalId) goal.copy(isCompleted = true) else goal
            }
        }
    }
}