package br.com.wheatherApp.data.model

import kotlinx.serialization.Serializable

@Serializable
class ForecastWeatherResponse (
    val timelinesData: TimelinesData,
    val location: LocationData
)