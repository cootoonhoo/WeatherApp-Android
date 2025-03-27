package br.com.wheatherApp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val time: String,
    val values: WeatherValues
)