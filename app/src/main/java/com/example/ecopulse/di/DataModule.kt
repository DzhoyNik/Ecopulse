package com.example.ecopulse.di

import com.example.ecopulse.data.repository.EcoRepositoryImpl
import com.example.ecopulse.domain.repository.EcoRepository
import com.example.ecopulse.domain.usecase.GetEcoTipsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideEcoRepository(): EcoRepository {
        return EcoRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideGetEcoTipsUseCase(repository: EcoRepository): GetEcoTipsUseCase {
        return GetEcoTipsUseCase(repository)
    }
}