package com.example.ecopulse.di

import com.example.ecopulse.domain.repository.EcoRepository
import com.example.ecopulse.domain.usecase.CompleteGoalUseCase
import com.example.ecopulse.domain.usecase.GetEcoGoalsUseCase
import com.example.ecopulse.domain.usecase.GetUserProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    @ViewModelScoped
    fun provideGetEcoGoalsUseCase(repository: EcoRepository): GetEcoGoalsUseCase {
        return GetEcoGoalsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetUserProfileUseCase(repository: EcoRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideCompleteGoalUseCase(repository: EcoRepository): CompleteGoalUseCase {
        return CompleteGoalUseCase(repository)
    }
}