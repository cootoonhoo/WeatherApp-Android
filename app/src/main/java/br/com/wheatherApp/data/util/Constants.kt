package br.com.wheatherApp.data.util

import br.com.wheatherApp.BuildConfig

object Constants {
    const val WHEATER_API_KEY  = BuildConfig.WHEATER_API_KEY
    const val API_REALTIME_URL = "${BuildConfig.WEATHER_API_URL}/current?"
    const val API_FORECAST_URL = "${BuildConfig.WEATHER_API_URL}/forecast/hourly?"
    const val GEOCODING_API_URL = "${BuildConfig.GEOCODING_API_URL}/json?"
    const val GEOCODING_API_KEY = BuildConfig.GEOCODING_API_KEY

}