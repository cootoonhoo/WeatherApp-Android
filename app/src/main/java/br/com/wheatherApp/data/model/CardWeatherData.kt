package br.com.wheatherApp.data.model

data class CardWeatherData(
    val cityName: String,
    val currentTemp: Int,
    val maxTemp: Int,
    val minTemp: Int,
    val rainningChance: Double,
    val status : String
)