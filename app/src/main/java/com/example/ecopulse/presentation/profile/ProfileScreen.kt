package com.example.ecopulse.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ecopulse.databinding.FragmentProfileBinding

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfileState.collectAsState()

    if (profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Используем AndroidView для внедрения XML-структуры
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                // Надуваем (inflate) XML разметку через ViewBinding
                val binding = FragmentProfileBinding.inflate(android.view.LayoutInflater.from(context))

                // Привязываем данные из доменной модели к XML TextView
                binding.tvUserName.text = profile?.name
                binding.tvUserEmail.text = profile?.email
                binding.tvEcoPoints.text = "Всего очков: ${profile?.ecoPoints} XP"
                binding.tvCompletedCount.text = "Выполнено целей: ${profile?.completedGoalsCount}"

                // Работаем со встроенным в XML ComposeView!
                binding.composeViewInXml.setContent {
                    MaterialTheme {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Уровень прогресса приложения (внутри ComposeView):")

                            // Отображаем уровень прогресса на основе уровня из твоей доменной модели levelProgress
                            LinearProgressIndicator(
                                progress = { profile?.levelProgress ?: 0f },
                                modifier = Modifier.fillMaxWidth().height(8.dp)
                            )

                            androidx.compose.material3.Button(
                                onClick = onBack,
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                            ) {
                                Text("Назад к целям")
                            }
                        }
                    }
                }

                binding.root // Возвращаем корневую XML View
            },
            update = { _ ->
                // Здесь можно обновлять XML View, если стейт поменялся динамически
            }
        )
    }
}