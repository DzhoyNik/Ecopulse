package com.example.ecopulse.data.model.mapper

import com.example.ecopulse.data.model.EcoGoalEntity
import com.example.ecopulse.data.model.UserProfileEntity
import com.example.ecopulse.domain.model.EcoGoal
import com.example.ecopulse.domain.model.UserProfile

fun EcoGoalEntity.toDomain(): EcoGoal {
    return EcoGoal(
        id = goalId.toString(),  // Any.toString() работает и для Long и для String
        title = titleText,
        description = subDescription,
        pointsReward = rewardAmount.toInt(),
        isCompleted = statusCompleted
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    // Вычисляем прогресс уровня на основе очков (допустим, каждый уровень — 1000 очков)
    val progress = (this.currentPoints % 1000) / 1000f

    return UserProfile(
        id = this.uid,
        name = this.fullName,
        email = this.accountEmail,
        ecoPoints = this.currentPoints,
        levelProgress = if (progress == 0f && this.currentPoints > 0) 1.0f else progress,
        completedGoalsCount = this.completedCount
    )
}