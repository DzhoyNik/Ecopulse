package com.example.ecopulse.presentation.goals

import com.example.ecopulse.BuildConfig
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopulse.domain.model.EcoGoal

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel
) {
    val goals by viewModel.goalsState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "EcoPulse Трекер", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)
            if (BuildConfig.IS_PREMIUM) {
                Badge { Text("PRO") }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(goals) { goal ->
                GoalCard(goal = goal, onComplete = { viewModel.completeGoal(goal.id) })
            }
        }
    }
}

@Composable
private fun GoalCard(goal: EcoGoal, onComplete: () -> Unit) {
    // animate*AsState — второй тип анимации
    val cardColor by animateColorAsState(
        targetValue = if (goal.isCompleted)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 600),
        label = "goalCardColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = goal.title, fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
                Text(text = goal.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "+${goal.pointsReward} XP", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = onComplete,
                enabled = !goal.isCompleted
            ) {
                Text(if (goal.isCompleted) "Готово" else "Выполнить")
            }
        }
    }
}