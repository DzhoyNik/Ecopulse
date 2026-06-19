package com.example.ecopulse.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class EcoSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Фоновая синхронизация эко-целей с сервером.
            // Реальная логика синхронизации добавляется здесь.
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}