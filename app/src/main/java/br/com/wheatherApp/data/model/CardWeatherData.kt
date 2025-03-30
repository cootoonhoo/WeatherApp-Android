package br.com.wheatherApp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CardWeatherData(
    val cityName: String,
    val countryCode: String,
    val currentTemp: Int,
    val maxTemp: Int,
    val minTemp: Int,
    val rainningChance: Double,
    val status: String
)