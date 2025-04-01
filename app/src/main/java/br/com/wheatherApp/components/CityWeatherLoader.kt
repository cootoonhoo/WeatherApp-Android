package br.com.wheatherApp.components

import City
import ErrorScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import br.com.wheatherApp.LoadingScreen
import br.com.wheatherApp.WeatherDetailScreen
import br.com.wheatherApp.data.api.getCurrentWeather
import br.com.wheatherApp.data.api.getHourlyForecastWeather
import br.com.wheatherApp.data.api.getDailyForecastWeather
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.data.model.DailyWeatherData
import br.com.wheatherApp.data.model.HourlyWeatherData
import kotlinx.coroutines.launch
@Composable
fun CityWeatherLoader(
    cityName: String,
    countryCode: String,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var weatherData by remember { mutableStateOf<CardWeatherData?>(null) }

    LaunchedEffect(cityName, countryCode) {
        isLoading = true
        error = null

        coroutineScope.launch {
            try {
                val city = City(cityName, countryCode)
                val currentResponse = getCurrentWeather(city, null)
                val forecastResponse = getHourlyForecastWeather(city, null)
                val dailyForecastResponse = getDailyForecastWeather(city, null)

                if (currentResponse != null && forecastResponse != null) {
                    val currentWeatherItem = currentResponse.data.firstOrNull()

                    if (currentWeatherItem != null) {
                        val currentTemp = currentWeatherItem.temp?.toInt() ?: 0

                        var maxTemp = Int.MIN_VALUE
                        var minTemp = Int.MAX_VALUE

                        forecastResponse.data?.forEach { forecast ->
                            forecast.temp?.let { temp ->
                                val tempInt = temp.toInt()
                                if (tempInt > maxTemp) maxTemp = tempInt
                                if (tempInt < minTemp) minTemp = tempInt
                            }
                        }

                        if (maxTemp == Int.MIN_VALUE) maxTemp = currentTemp
                        if (minTemp == Int.MAX_VALUE) minTemp = currentTemp

                        val rainChance = forecastResponse.data?.firstOrNull()?.pop?.toDouble()?.div(100)
                            ?: (currentWeatherItem.precip?.toDouble()?.coerceAtMost(100.0)?.div(100) ?: 0.0)

                        val airQuality = currentWeatherItem.aqi

                        val hourlyWeatherData = forecastResponse.data?.map { forecast ->
                            HourlyWeatherData(
                                temp = forecast.temp,
                                time = forecast.datetime ?: "N/A",
                                humidity = forecast.rh,
                                windSpeed = forecast.windSpeed,
                                rainChance = forecast.pop?.toDouble(),
                                uvIndex = forecast.uv,
                                airQuality = airQuality
                            )
                        } ?: emptyList()

                        val dailyWeatherData = dailyForecastResponse?.data?.map { dailyForecast ->
                            DailyWeatherData(
                                date = dailyForecast.validDate ?: "N/A",
                                maxTemp = dailyForecast.maxTemp?.toInt() ?: currentTemp,
                                minTemp = dailyForecast.minTemp?.toInt() ?: currentTemp,
                                description = dailyForecast.weather?.description,
                                rainChance = dailyForecast.pop?.toDouble()?.div(100)
                            )
                        } ?: emptyList()

                        weatherData = CardWeatherData(
                            cityName = currentWeatherItem.cityName ?: cityName,
                            countryCode = currentWeatherItem.countryCode ?: countryCode,
                            currentTemp = currentTemp,
                            maxTemp = maxTemp,
                            minTemp = minTemp,
                            rainningChance = rainChance,
                            status = currentWeatherItem.weather?.description ?: "Céu limpo",
                            statusCode = currentWeatherItem.weather?.code,
                            windSpeed = currentWeatherItem.windSpeed,
                            humidity = currentWeatherItem.rh,
                            pressure = currentWeatherItem.pres?.toDouble(),
                            windGustSpeed = currentWeatherItem.gust,
                            solarRadiation = currentWeatherItem.solarRadiation,
                            visibility = currentWeatherItem.vis?.toDouble(),
                            uvIndex = currentWeatherItem.uv,
                            airQuality = currentWeatherItem.aqi,
                            hourlyWeatherData = hourlyWeatherData,
                            dailyWeatherData = dailyWeatherData
                        )
                    }
                    isLoading = false
                } else {
                    error = "Não foi possível carregar os dados do clima para $cityName"
                    isLoading = false
                }
            } catch (e: Exception) {
                error = "Erro ao carregar dados do clima: ${e.message}"
                isLoading = false
            }
        }
    }

    when {
        isLoading -> {
            LoadingScreen()
        }
        error != null -> {
            ErrorScreen(
                message = error ?: "Erro desconhecido",
                onRetry = onBackClick
            )
        }
        weatherData != null -> {
            WeatherDetailScreen(
                weatherData = weatherData!!,
                onBackClick = onBackClick
            )
        }
    }
}