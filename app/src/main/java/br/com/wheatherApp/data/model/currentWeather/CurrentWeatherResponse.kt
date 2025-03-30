package br.com.wheatherApp.data.model.currentWeather

import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherResponse(
    val count: Int,
    val data: List<CurrentWeatherData>
)