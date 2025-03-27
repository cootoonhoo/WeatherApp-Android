package br.com.wheatherApp.data.model

import kotlinx.serialization.Serializable

@Serializable
class TimelinesData (
    val time : String,
    val values : Array<ForecastWeatherData>
)