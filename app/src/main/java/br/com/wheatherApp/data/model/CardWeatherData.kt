package br.com.wheatherApp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CardWeatherData(
    val cityName: String,
    val countryCode: String,
    val currentTemp: Int,
    var maxTemp: Int,
    var minTemp: Int,
    val rainningChance: Double,
    val status: String,
    val windSpeed: Double?,
    val humidity: Int?,
    val pressure: Double?,
    val windGustSpeed: Double?,
    val solarRadiation: Double?,
    val visibility: Double?,
    val uvIndex: Int?,
    val airQuality: Int?,
    val hourlyWeatherData: List<HourlyWeatherData>?
)
