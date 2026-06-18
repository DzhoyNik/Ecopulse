package com.example.ecopulse.data.repository

import com.example.ecopulse.data.model.EcoGoalEntity
import com.example.ecopulse.data.model.UserProfileEntity
import com.example.ecopulse.data.model.mapper.toDomain
import com.example.ecopulse.domain.repository.EcoRepository
import com.example.ecopulse.domain.model.EcoGoal
import com.example.ecopulse.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class EcoRepositoryImpl : EcoRepository {

    // Имитируем базу данных/сетевой кэш в оперативной памяти
    private val profileState = MutableStateFlow(
        UserProfileEntity("user_77", "Эко-Активист", "eco@pulse.com", 350, 2)
    )

    private val goalsState = MutableStateFlow(
        listOf(
            EcoGoalEntity("1", "Сдать пластик", "Соберите и отнесите ПЭТ-бутылки в пункт приема", 100, false),
            EcoGoalEntity("2", "День без автомобиля", "Воспользуйтесь велосипедом или самокатом", 150, false),
            EcoGoalEntity("3", "Энергосбережение", "Выключайте технику из розетки на ночь", 50, true),
            EcoGoalEntity("4", "Эко-сумка", "Откажитесь от пакета на кассе супермаркета", 80, false)
        )
    )

    override fun getUserProfile(): Flow<UserProfile> {
        return profileState.map { it.toDomain() }
    }

    override fun getEcoGoals(): Flow<List<EcoGoal>> {
        return goalsState.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun completeGoal(goalId: String) {
        // Находим цель, меняем статус
        var pointsToAdd = 0
        goalsState.update { currentList ->
            currentList.map { goal ->
                if (goal.goalId == goalId && !goal.statusCompleted) {
                    pointsToAdd = goal.rewardAmount
                    goal.copy(statusCompleted = true)
                } else {
                    goal
                }
            }
        }

        // Если цель была успешно выполнена впервые, обновляем профиль (добавляем очки)
        if (pointsToAdd > 0) {
            profileState.update { currentProfile ->
                currentProfile.copy(
                    currentPoints = currentProfile.currentPoints + pointsToAdd,
                    completedCount = currentProfile.completedCount + 1
                )
            }
        }
    }
}