package br.com.wheatherApp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val cityName: String,
    val currentTemp: Int,
    val maxTemp: Int,
    val minTemp: Int,
    val ranningChance: Double,
    val status: String
)
