package com.example.ecopulse.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecopulse.domain.model.UserProfile
import com.example.ecopulse.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    // Превращаем Flow от UseCase в StateFlow для удобного наблюдения в UI
    val userProfileState: StateFlow<UserProfile?> = getUserProfileUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}