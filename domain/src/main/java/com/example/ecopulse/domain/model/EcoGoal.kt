package com.example.ecopulse.domain.model

data class EcoGoal(
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val isCompleted: Boolean = false
)