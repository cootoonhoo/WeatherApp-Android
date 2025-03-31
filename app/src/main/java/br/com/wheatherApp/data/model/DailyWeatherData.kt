
package br.com.wheatherApp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyWeatherData(
    val date: String,
    val maxTemp: Int,
    val minTemp: Int,
    val description: String?,
    val rainChance: Double?
)