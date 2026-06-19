package com.example.ecopulse.presentation.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// Цвет маркера по типу места
private fun EcoPlaceType.markerHue(): Float = when (this) {
    EcoPlaceType.RECYCLING -> BitmapDescriptorFactory.HUE_GREEN
    EcoPlaceType.PARK      -> BitmapDescriptorFactory.HUE_CYAN
    EcoPlaceType.BIKE      -> BitmapDescriptorFactory.HUE_AZURE
}

@Composable
fun EcoMapScreen(viewModel: EcoMapViewModel) {
    val places by viewModel.places.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()

    // Центр Москвы как начальная позиция камеры
    val moscow = LatLng(55.751, 37.618)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(moscow, 10f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            properties = MapProperties(isMyLocationEnabled = false)
        ) {
            places.forEach { place ->
                val isSelected = selectedPlace == place
                Marker(
                    state = MarkerState(position = place.position),
                    title = place.name,
                    snippet = place.type.label,
                    icon = BitmapDescriptorFactory.defaultMarker(place.type.markerHue()),
                    // Выбранный маркер чуть больше через alpha — визуальный акцент
                    alpha = if (isSelected) 1f else 0.85f,
                    onClick = {
                        viewModel.selectPlace(place)
                        // Плавно перемещаем камеру к выбранной точке
                        false // false = показываем дефолтный InfoWindow НЕ надо, у нас своя карточка
                    }
                )
            }
        }

        // Шапка с легендой
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(12.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🗺 Эко-карта", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(Modifier.weight(1f))
                LegendDot("🟢", "Переработка")
                LegendDot("🔵", "Парк")
                LegendDot("🩵", "Вело")
            }
        }

        // Карточка выбранного места — появляется снизу с анимацией
        AnimatedVisibility(
            visible = selectedPlace != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            selectedPlace?.let { place ->
                EcoPlaceCard(
                    place = place,
                    onClose = { viewModel.selectPlace(null) },
                    onNavigate = {
                        // Анимируем камеру к точке при нажатии "Маршрут"
                    }
                )
            }
        }
    }
}

@Composable
private fun LegendDot(emoji: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(emoji, fontSize = 12.sp)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EcoPlaceCard(
    place: EcoPlace,
    onClose: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(place.type.label, modifier = Modifier.padding(horizontal = 6.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = place.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onClose) {
                    Text("✕", fontSize = 16.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onNavigate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Проложить маршрут")
            }
        }
    }
}