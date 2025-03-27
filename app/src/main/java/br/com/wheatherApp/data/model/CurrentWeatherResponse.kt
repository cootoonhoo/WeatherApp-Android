package br.com.wheatherApp.data.model
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherResponse(
    val data: WeatherData,
    val location: LocationData
)
