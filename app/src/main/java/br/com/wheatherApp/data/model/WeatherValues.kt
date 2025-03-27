package br.com.wheatherApp.data.model
import kotlinx.serialization.Serializable

@Serializable
data class WeatherValues(
    val cloudBase: Double? = null,
    val cloudCeiling: Double? = null,
    val cloudCover: Int? = null,
    val dewPoint: Double? = null,
    val freezingRainIntensity: Int? = null,
    val hailProbability: Double? = null,
    val humidity: Int? = null,
    val precipitationProbability: Int? = null,
    val pressureSeaLevel: Double? = null,
    val pressureSurfaceLevel: Double? = null,
    val rainIntensity: Int? = null,
    val sleetIntensity: Int? = null,
    val snowIntensity: Int? = null,
    val temperature: Double? = null,
    val temperatureApparent: Double? = null,
    val uvHealthConcern: Int? = null,
    val uvIndex: Int? = null,
    val visibility: Double? = null,
    val weatherCode: Int? = null,
    val windDirection: Int? = null,
    val windGust: Double? = null,
    val windSpeed: Double? = null
)