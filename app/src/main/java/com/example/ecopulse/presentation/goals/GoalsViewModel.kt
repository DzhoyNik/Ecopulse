package com.example.ecopulse.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecopulse.domain.model.EcoGoal
import com.example.ecopulse.domain.usecase.CompleteGoalUseCase
import com.example.ecopulse.domain.usecase.GetEcoGoalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val getEcoGoalsUseCase: GetEcoGoalsUseCase,
    private val completeGoalUseCase: CompleteGoalUseCase
) : ViewModel() {

    private val _goalsState = MutableStateFlow<List<EcoGoal>>(emptyList())
    val goalsState = _goalsState.asStateFlow()

    init {
        getEcoGoalsUseCase()
            .onEach { list -> _goalsState.value = list }
            .launchIn(viewModelScope)
    }

    fun completeGoal(goalId: String) {
        viewModelScope.launch {
            completeGoalUseCase(goalId) // вызов через domain Use Case, не напрямую
        }
    }
}