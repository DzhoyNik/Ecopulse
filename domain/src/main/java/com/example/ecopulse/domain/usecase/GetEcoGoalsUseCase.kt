package com.example.ecopulse.domain.usecase

import com.example.ecopulse.domain.model.EcoGoal
import com.example.ecopulse.domain.repository.EcoRepository
import kotlinx.coroutines.flow.Flow

class GetEcoGoalsUseCase(private val repository: EcoRepository) {
    operator fun invoke(): Flow<List<EcoGoal>> {
        return repository.getEcoGoals()
    }
}