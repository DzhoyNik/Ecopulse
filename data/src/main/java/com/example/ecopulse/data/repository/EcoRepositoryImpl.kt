package com.example.ecopulse.data.repository

import com.example.ecopulse.data.model.EcoGoalEntity
import com.example.ecopulse.data.model.UserProfileEntity
import com.example.ecopulse.data.model.mapper.toDomain
import com.example.ecopulse.domain.model.EcoGoal
import com.example.ecopulse.domain.model.EcoTip
import com.example.ecopulse.domain.model.UserProfile
import com.example.ecopulse.domain.repository.EcoRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class EcoRepositoryImpl : EcoRepository {

    private val db = FirebaseFirestore.getInstance()

    // Реальное чтение из Firestore с live-обновлениями через callbackFlow
    override fun getUserProfile(): Flow<UserProfile> = callbackFlow {
        val listener = db.collection("users")
            .document("user_77")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val entity = snapshot.toObject(UserProfileEntity::class.java)
                entity?.let { trySend(it.toDomain()) }
            }
        awaitClose { listener.remove() }
    }

    override fun getEcoGoals(): Flow<List<EcoGoal>> = callbackFlow {
        val listener = db.collection("goals")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val goals = snapshot.documents
                    .mapNotNull { it.toObject(EcoGoalEntity::class.java) }
                    .map { it.toDomain() }
                trySend(goals)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun completeGoal(goalId: String) {
        // Ищем документ по значению поля goalId, а не по имени документа
        val snapshot = db.collection("goals")
            .whereEqualTo("goalId", goalId.toLongOrNull() ?: goalId)
            .get().await()

        val doc = snapshot.documents.firstOrNull() ?: return
        val goal = doc.toObject(EcoGoalEntity::class.java) ?: return
        if (goal.statusCompleted) return

        doc.reference.update("statusCompleted", true).await()

        db.collection("users").document("user_77")
            .update(
                "currentPoints", com.google.firebase.firestore.FieldValue.increment(goal.rewardAmount.toLong()),
                "completedCount", com.google.firebase.firestore.FieldValue.increment(1)
            ).await()
    }

    override fun getEcoTips(): Flow<List<EcoTip>> = flowOf(
        listOf(
            EcoTip("1", "Как правильно сортировать пластик?", "Ищите маркировку 1 (PET) и 2 (HDPE). Обязательно споласкивайте тару перед сдачей.", "Сортировка"),
            EcoTip("2", "Энергосбережение дома", "Замените лампы накаливания на светодиодные (LED) — это снизит потребление энергии до 80%.", "Дом"),
            EcoTip("3", "Осознанное потребление", "Перед покупкой вещи сделайте паузу на 24 часа. Это защитит от импульсивных трат и лишнего мусора.", "Шопинг")
        )
    )
}