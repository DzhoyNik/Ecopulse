package com.example.ecopulse.presentation.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class EcoPlace(
    val name: String,
    val description: String,
    val position: LatLng,
    val type: EcoPlaceType
)

enum class EcoPlaceType(val label: String) {
    RECYCLING("Переработка"),
    PARK("Парк"),
    BIKE("Велодорожка")
}

@HiltViewModel
class EcoMapViewModel @Inject constructor() : ViewModel() {

    // Эко-точки Москвы (пример реальных координат)
    private val _places = MutableStateFlow(
        listOf(
            EcoPlace("Пункт приёма «ЭкоЛайн»", "Стекло, пластик, металл", LatLng(55.753, 37.622), EcoPlaceType.RECYCLING),
            EcoPlace("Парк Горького", "Экологическая зона отдыха", LatLng(55.729, 37.601), EcoPlaceType.PARK),
            EcoPlace("Велопарковка ВДНХ", "Городская велодорожка", LatLng(55.833, 37.631), EcoPlaceType.BIKE),
            EcoPlace("Пункт приёма батареек", "ИКЕА Химки", LatLng(55.895, 37.370), EcoPlaceType.RECYCLING),
            EcoPlace("Природный заказник Лосиный остров", "Охраняемая природная зона", LatLng(55.873, 37.757), EcoPlaceType.PARK),
        )
    )
    val places = _places.asStateFlow()

    private val _selectedPlace = MutableStateFlow<EcoPlace?>(null)
    val selectedPlace = _selectedPlace.asStateFlow()

    fun selectPlace(place: EcoPlace?) {
        _selectedPlace.value = place
    }
}