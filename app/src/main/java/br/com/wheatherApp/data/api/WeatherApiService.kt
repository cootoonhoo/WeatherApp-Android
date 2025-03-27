package br.com.wheatherApp.data.api

import br.com.wheatherApp.data.model.CurrentWeatherResponse
import br.com.wheatherApp.data.model.ForecastWeatherResponse
import br.com.wheatherApp.data.model.LocationData
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


