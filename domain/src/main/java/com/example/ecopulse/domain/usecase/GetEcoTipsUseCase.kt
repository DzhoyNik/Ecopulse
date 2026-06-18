package com.example.ecopulse.domain.usecase

import com.example.ecopulse.domain.model.EcoTip
import com.example.ecopulse.domain.repository.EcoRepository
import kotlinx.coroutines.flow.Flow

class GetEcoTipsUseCase(private val repository: EcoRepository) {
    operator fun invoke(): Flow<List<EcoTip>> {
        return repository.getEcoTips()
    }
}