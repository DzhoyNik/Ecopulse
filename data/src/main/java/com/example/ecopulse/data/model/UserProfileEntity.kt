package com.example.ecopulse.data.model

import com.google.firebase.firestore.PropertyName

data class UserProfileEntity(
    @get:PropertyName("uid") @set:PropertyName("uid")
    var uid: String = "",

    @get:PropertyName("fullName") @set:PropertyName("fullName")
    var fullName: String = "",

    @get:PropertyName("accountEmail") @set:PropertyName("accountEmail")
    var accountEmail: String = "",

    @get:PropertyName("currentPoints") @set:PropertyName("currentPoints")
    var currentPoints: Int = 0,

    @get:PropertyName("completedCount") @set:PropertyName("completedCount")
    var completedCount: Int = 0
)