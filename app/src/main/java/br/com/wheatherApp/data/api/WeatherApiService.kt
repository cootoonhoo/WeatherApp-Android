package br.com.wheatherApp.data.api

import City
import br.com.wheatherApp.data.model.Location
import br.com.wheatherApp.data.model.currentWeather.CurrentWeatherResponse
import br.com.wheatherApp.data.model.forecastWeather.ForecastWeatherResponse
import br.com.wheatherApp.data.util.Constants
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

// Testing
suspend fun main()
{
    println(getCurrentWeather(City("Uberlandia","br"),null,))
    println(getForecastWeather(City("Uberlandia","br"),null,))
}

suspend fun getCurrentWeather(city: City?, location : Location?) : CurrentWeatherResponse? {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(Constants.API_REALTIME_URL) {
        url {
            parameters.append("key", Constants.WHEATER_API_KEY)
            if(city != null && city.cityName.isNotEmpty() && city.countryCode.isNotEmpty())
            {
                parameters.append("city",city.cityName)
                parameters.append("country",city.countryCode)
            }
            else if(location != null)
            {
                parameters.append("lat",location.latitude.toString())
                parameters.append("lon",location.longitude.toString())
            }
        }
    }
    if(response.status.value != 200)
        return  null;

    return json.decodeFromString<CurrentWeatherResponse>(response.bodyAsText())
}

suspend fun getForecastWeather(city: City?, location : Location?) : ForecastWeatherResponse? {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(Constants.API_FORECAST_URL) {
        url {
            parameters.append("key", Constants.WHEATER_API_KEY)
            parameters.append("days", "7")
            if(city != null && city.cityName.isNotEmpty() && city.countryCode.isNotEmpty())
            {
                parameters.append("city",city.cityName)
                parameters.append("country",city.countryCode)
            }
            else if(location != null)
            {
                parameters.append("lat",location.latitude.toString())
                parameters.append("lon",location.longitude.toString())
            }
        }
    }
    if(response.status.value != 200)
        return  null;

    return json.decodeFromString<ForecastWeatherResponse>(response.bodyAsText())
}
