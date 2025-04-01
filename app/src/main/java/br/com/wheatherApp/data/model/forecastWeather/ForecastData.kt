package br.com.wheatherApp.data.model.forecastWeather

import br.com.wheatherApp.data.model.Weather
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastData(
    @SerialName("app_max_temp")
    val apparentMaxTemp: Double? = null,
    @SerialName("app_min_temp")
    val apparentMinTemp: Double? = null,
    val clouds: Int? = null,
    @SerialName("clouds_hi")
    val cloudsHigh: Int? = null,
    @SerialName("clouds_low")
    val cloudsLow: Int? = null,
    @SerialName("clouds_mid")
    val cloudsMid: Int? = null,
    val datetime: String? = null,
    val dewpt: Double? = null,
    @SerialName("high_temp")
    val highTemp: Double? = null,
    @SerialName("low_temp")
    val lowTemp: Double? = null,
    @SerialName("max_dhi")
    val maxDhi: Double? = null,
    @SerialName("max_temp")
    val maxTemp: Double? = null,
    @SerialName("min_temp")
    val minTemp: Double? = null,
    @SerialName("moon_phase")
    val moonPhase: Double? = null,
    @SerialName("moon_phase_lunation")
    val moonPhaseLunation: Double? = null,
    @SerialName("moonrise_ts")
    val moonriseTs: Long? = null,
    @SerialName("moonset_ts")
    val moonsetTs: Long? = null,
    val ozone: Int? = null,
    val pop: Int? = null,
    val precip: Double? = null,
    val pres: Double? = null,
    val rh: Int? = null,
    val slp: Double? = null,
    val snow: Double? = null,
    @SerialName("snow_depth")
    val snowDepth: Double? = null,
    @SerialName("sunrise_ts")
    val sunriseTs: Long? = null,
    @SerialName("sunset_ts")
    val sunsetTs: Long? = null,
    val temp: Double? = null,
    val ts: Long? = null,
    val uv: Int? = null,
    @SerialName("valid_date")
    val validDate: String? = null,
    val vis: Double? = null,
    val weather: Weather? = null,
    @SerialName("wind_cdir")
    val windDirectionAbbreviated: String? = null,
    @SerialName("wind_cdir_full")
    val windDirectionFull: String? = null,
    @SerialName("wind_dir")
    val windDirection: Int? = null,
    @SerialName("wind_gust_spd")
    val windGustSpeed: Double? = null,
    @SerialName("wind_spd")
    val windSpeed: Double? = null
)