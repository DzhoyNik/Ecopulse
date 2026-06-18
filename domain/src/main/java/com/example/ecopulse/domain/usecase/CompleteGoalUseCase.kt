package com.example.ecopulse.domain.usecase

import com.example.ecopulse.domain.repository.EcoRepository

class CompleteGoalUseCase(private val repository: EcoRepository) {
    suspend operator fun invoke(goalId: String) {
        repository.completeGoal(goalId)
    }
}