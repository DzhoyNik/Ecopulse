package com.example.ecopulse.data.model

import com.google.firebase.firestore.PropertyName

data class EcoGoalEntity(
    @get:PropertyName("goalId") @set:PropertyName("goalId")
    var goalId: Any = "",

    @get:PropertyName("titleText") @set:PropertyName("titleText")
    var titleText: String = "",

    @get:PropertyName("subDescription") @set:PropertyName("subDescription")
    var subDescription: String = "",

    @get:PropertyName("rewardAmount") @set:PropertyName("rewardAmount")
    var rewardAmount: Long = 0L,

    @get:PropertyName("statusCompleted") @set:PropertyName("statusCompleted")
    var statusCompleted: Boolean = false
)