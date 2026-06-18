package com.example.ecopulse.data.model

data class UserProfileEntity(
    val uid: String,
    val fullName: String,
    val accountEmail: String,
    val currentPoints: Int,
    val completedCount: Int
)