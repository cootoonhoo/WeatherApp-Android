package br.com.wheatherApp.data.model.forecastWeather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastWeatherResponse(
    @SerialName("city_name")
    val cityName: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    val data: List<ForecastData>? = null,
    val lat: String? = null,
    val lon: String? = null,
    @SerialName("state_code")
    val stateCode: String? = null,
    val timezone: String? = null
)