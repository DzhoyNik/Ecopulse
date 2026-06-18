package com.example.ecopulse.presentation.stats

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatsScreen(
    viewModel: StatsViewModel
) {
    val completedCount by viewModel.completedGoalsCount.collectAsState()
    val tips by viewModel.ecoTips.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Статистика", "Эко-Советы")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Аналитика & Инфо", fontSize = 26.sp, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Переключатель вкладок
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 16.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Отображение контента в зависимости от вкладки
        if (selectedTab == 0) {
            // ВКЛАДКА 1: Графика и прогресс
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ваш вклад в экологию", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "$completedCount", fontSize = 48.sp, style = MaterialTheme.typography.displayLarge)
                        Text("выполненных эко-задач", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Имитация мини-графика активности
                Text("Активность за неделю", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val days = listOf("Пн" to 0.3f, "Вт" to 0.6f, "Ср" to 0.2f, "Чт" to 0.9f, "Пт" to 0.5f, "Сб" to 0.1f, "Вс" to 0.7f)
                    days.forEach { (day, weight) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .fillMaxHeight(weight)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(day, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        } else {
            // ВКЛАДКА 2: Раскрывающийся список полезных советов
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(tips) { tip ->
                    var isExpanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth().animateContentSize().clickable { isExpanded = !isExpanded }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                Text(tip.category, modifier = Modifier.padding(4.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = tip.title, style = MaterialTheme.typography.titleMedium)

                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = tip.content, color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}