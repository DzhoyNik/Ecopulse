package com.example.ecopulse.domain.repository

import com.example.ecopulse.domain.model.EcoGoal
import com.example.ecopulse.domain.model.EcoTip
import com.example.ecopulse.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface EcoRepository {
    // Получение профиля пользователя в реальном времени
    fun getUserProfile(): Flow<UserProfile>

    // Получение списка эко-целей для Главного экрана
    fun getEcoGoals(): Flow<List<EcoGoal>>

    // Отметка цели как выполненной
    suspend fun completeGoal(goalId: String)

    fun getEcoTips(): kotlinx.coroutines.flow.Flow<List<EcoTip>>
}