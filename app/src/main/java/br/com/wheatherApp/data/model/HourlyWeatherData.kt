package br.com.wheatherApp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HourlyWeatherData(
    val temp: Double?,
    val time: String,
    val humidity: Int?,
    val windSpeed: Double?,
    val rainChance: Double?,
    val uvIndex: Int?,
    val airQuality: Int?
)
