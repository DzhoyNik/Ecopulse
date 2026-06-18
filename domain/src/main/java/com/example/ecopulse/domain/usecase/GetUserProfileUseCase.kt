package com.example.ecopulse.domain.usecase

import com.example.ecopulse.domain.model.UserProfile
import com.example.ecopulse.domain.repository.EcoRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(private val repository: EcoRepository) {
    operator fun invoke(): Flow<UserProfile> {
        return repository.getUserProfile()
    }
}