package br.com.wheatherApp.data.api

import br.com.wheatherApp.data.model.CurrentWeatherData
import br.com.wheatherApp.data.model.CurrentWeatherResponse
import br.com.wheatherApp.data.model.ForecastWeatherData
import br.com.wheatherApp.data.model.ForecastWeatherResponse
import br.com.wheatherApp.data.model.LocationData
import br.com.wheatherApp.data.model.TimelinesData
import br.com.wheatherApp.data.model.WeatherValues
import br.com.wheatherApp.data.util.Constants
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

const val USE_MOCK_VALUES = false;

suspend fun getCurrentWeather(city: String, location : LocationData?) : CurrentWeatherResponse? {
    if(USE_MOCK_VALUES)
        return getMockCurrentWeather(city, location)

    val client = HttpClient(CIO)
    val locationInfo: String

    if (city.isNotEmpty()) {
        locationInfo = city;
    } else if (location != null) {
        locationInfo = "${location.lat},${location.lon}"
    } else
        return null;

    val response: HttpResponse = client.get(Constants.API_REALTIME_URL) {
        url {
            parameters.append("apikey", Constants.API_KEY)
            parameters.append("location", locationInfo)
            parameters.append("units", "metric")
        }
    }
    if(response.status.value != 200)
        return  null;
    return json.decodeFromString<CurrentWeatherResponse>(response.bodyAsText())
}

suspend fun getForecastWeather(city: String, location : LocationData?) : ForecastWeatherResponse? {
    if(USE_MOCK_VALUES)
        return getMockForecastWeather(city, location)

    val client = HttpClient(CIO)
    val locationInfo: String

    if (city.isNotEmpty()) {
        locationInfo = city;
    } else if (location != null) {
        locationInfo = "${location.lat},${location.lon}"
    } else
        return null;

    val response: HttpResponse = client.get(Constants.API_FORECAST_URL) {
        headers {
            append(HttpHeaders.AcceptEncoding, "deflate, gzip, br")
        }
        url {
            parameters.append("apikey", Constants.API_KEY)
            parameters.append("location", locationInfo)
            parameters.append("units", "metric")
            parameters.append("timesteps", "1d")
        }
    }

    /** Claude 3.5 Hakiu - Início
     * Estou recebendo esse erro ao rodar a função getForecastWeather():
     * Exception in thread "main" kotlinx.serialization.json.internal.JsonDecodingException: Unexpected JSON token at offset 0: Expected start of the object '{', but had '' instead at path: $
     * JSON input:      ????_??y???u?n^H....
     */
    val responseBody = if (response.headers["Content-Encoding"]?.contains("gzip") == true) {
        val gzipInputStream = GZIPInputStream(ByteArrayInputStream(response.bodyAsBytes()))
        gzipInputStream.bufferedReader().use { it.readText() }
    } else {
        return  null;
    }
    /** Claude 3.5 Hakiu - Final */

    if(response.status.value != 200)
        return  null;

    return json.decodeFromString<ForecastWeatherResponse>(responseBody)
}
/** Claude 3.5 Hakiu - Início
 * Faça um função que gera um Mock das funções getForecastWeather e getCurrentWeather no arquivo WeatherApiService
 */

suspend fun getMockCurrentWeather(city: String, location: LocationData?): CurrentWeatherResponse {
    // Gerar dados fictícios para resposta de clima atual
    val mockLocation = location ?: LocationData(
        lat = -23.5505,
        lon = -46.6333,
        name = city.ifEmpty { "São Paulo" },
        type = "city"
    )

    val mockWeatherValues = WeatherValues(
        temperature = 22.5,
        temperatureApparent = 24.0,
        humidity = 65,
        windSpeed = 10.5,
        windDirection = 180,
        cloudCover = 30,
        uvIndex = 5,
        weatherCode = 1000, // Código para céu limpo
        pressureSeaLevel = 1013.25,
        visibility = 10.0
    )

    val mockCurrentWeatherData = CurrentWeatherData(
        time = "2024-03-27T12:00:00Z",
        values = mockWeatherValues
    )

    return CurrentWeatherResponse(
        data = mockCurrentWeatherData,
        location = mockLocation
    )
}

suspend fun getMockForecastWeather(city: String, location: LocationData?): ForecastWeatherResponse {
    // Gerar dados fictícios para previsão do tempo
    val mockLocation = location ?: LocationData(
        lat = -23.5505,
        lon = -46.6333,
        name = city.ifEmpty { "São Paulo" },
        type = "city"
    )

    // Criar um array de previsões para 7 dias
    val mockForecastData = Array(7) { index ->
        ForecastWeatherData(
            temperatureAvg = 22.5 + (Math.random() * 5 - 2.5), // Variação de temperatura
            temperatureMin = 18.0 + (Math.random() * 4 - 2.0),
            temperatureMax = 28.0 + (Math.random() * 4 - 2.0),
            precipitationProbabilityAvg = Math.random() * 30.0, // Probabilidade de precipitação
            weatherCodeMax = when {
                index % 3 == 0 -> 1000 // Céu limpo
                index % 3 == 1 -> 4000 // Parcialmente nublado
                else -> 6000 // Nublado
            },
            windSpeedAvg = 10.0 + (Math.random() * 5 - 2.5),
            humidityAvg = 60 + (Math.random() * 20 - 10).toInt()
        )
    }

    val mockTimelinesData = TimelinesData(
        time = "2024-03-27T12:00:00Z",
        values = mockForecastData
    )

    return ForecastWeatherResponse(
        timelinesData = mockTimelinesData,
        location = mockLocation
    )
}

/** Claude 3.5 Hakiu - Final */