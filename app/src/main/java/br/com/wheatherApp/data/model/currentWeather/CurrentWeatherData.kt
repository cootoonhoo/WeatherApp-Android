package br.com.wheatherApp.data.model.currentWeather

import br.com.wheatherApp.data.model.Weather
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherData(
    @SerialName("app_temp")
    val apparentTemperature: Double? = null,
    val aqi: Int? = null,
    @SerialName("city_name")
    val cityName: String? = null,
    val clouds: Int? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    val datetime: String? = null,
    val dewpt: Double? = null,
    val dhi: Int? = null,
    val dni: Int? = null,
    @SerialName("elev_angle")
    val elevationAngle: Double? = null,
    val ghi: Int? = null,
    val gust: Double? = null,
    @SerialName("h_angle")
    val horizontalAngle: Double? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    @SerialName("ob_time")
    val observationTime: String? = null,
    val pod: String? = null,
    val precip: Double? = null,
    val pres: Double? = null,
    val rh: Int? = null,
    val slp: Double? = null,
    val snow: Int? = null,
    @SerialName("solar_rad")
    val solarRadiation: Double? = null,
    val sources: List<String>? = null,
    @SerialName("state_code")
    val stateCode: String? = null,
    val station: String? = null,
    val sunrise: String? = null,
    val sunset: String? = null,
    val temp: Double? = null,
    val timezone: String? = null,
    val ts: Long? = null,
    val uv: Int? = null,
    val vis: Int? = null,
    val weather: Weather? = null,
    @SerialName("wind_cdir")
    val windDirectionAbbreviated: String? = null,
    @SerialName("wind_cdir_full")
    val windDirectionFull: String? = null,
    @SerialName("wind_dir")
    val windDirection: Int? = null,
    @SerialName("wind_spd")
    val windSpeed: Double? = null
)
