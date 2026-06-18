package com.example.ecopulse.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val ecoPoints: Int,        // Общее количество набранных эко-очков
    val levelProgress: Float,  // Значение от 0.0f до 1.0f для кругового прогресс-бара
    val completedGoalsCount: Int
)