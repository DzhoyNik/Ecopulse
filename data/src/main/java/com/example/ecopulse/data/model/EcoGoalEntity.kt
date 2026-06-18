package com.example.ecopulse.data.model

data class EcoGoalEntity(
    val goalId: String,
    val titleText: String,
    val subDescription: String,
    val rewardAmount: Int,
    val statusCompleted: Boolean
)