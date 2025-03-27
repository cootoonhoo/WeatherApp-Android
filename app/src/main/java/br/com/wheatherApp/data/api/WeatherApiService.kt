package br.com.wheatherApp.data.api

import br.com.wheatherApp.data.model.CurrentWeatherResponse
import br.com.wheatherApp.data.model.LocationData
import br.com.wheatherApp.data.util.Constants
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

// Only for testing
suspend fun main() {
    val response = getCurrentWeather("SaoPaulo", null)
    println(response)
}

suspend fun getCurrentWeather(city: String, location : LocationData?) : CurrentWeatherResponse? {
    val client = HttpClient(CIO)
    val locationInfo: String

    if (!city.isEmpty()) {
        locationInfo = city;
    } else if (location != null) {
        locationInfo = "${location.lat},${location.lon}"
    } else
        return null;

    val response: HttpResponse = client.get(Constants.API_REALTIME_URL) {
        url {
            parameters.append("apikey", Constants.API_KEY)
            parameters.append("location", "Sao Paulo")
            parameters.append("units", "metric")
        }
    }

    return json.decodeFromString<CurrentWeatherResponse>(response.bodyAsText())
}

