package com.example.ecopulse.di

import com.example.ecopulse.data.repository.EcoRepositoryImpl
import com.example.ecopulse.domain.repository.EcoRepository
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
}