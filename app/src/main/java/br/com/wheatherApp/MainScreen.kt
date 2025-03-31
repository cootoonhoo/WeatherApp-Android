package br.com.wheatherApp

import MainViewModel
import SearchBarComponent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import br.com.wheatherApp.components.FavoriteCitiesSection
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.wheatherApp.components.WeatherCardComponent
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.data.model.HourlyWeatherData
import br.com.wheatherApp.data.model.currentWeather.CurrentWeatherResponse
import br.com.wheatherApp.data.model.forecastWeather.ForecastWeatherResponse

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    latitude: Double? = null,
    longitude: Double? = null,
    currentWeatherData: CurrentWeatherResponse? = null,
    forecastWeatherData: ForecastWeatherResponse? = null,
    isLoading: Boolean = false,
    error: String? = null,
    onCardClick: (CardWeatherData) -> Unit
) {
    val favoriteWeatherData by viewModel.favoriteWeatherData.collectAsState()
    val isLoadingFavorites by viewModel.isLoadingFavorites.collectAsState()
    val loadingError by viewModel.loadingError.collectAsState()

    fun createWeatherCardData(
        currentData: CurrentWeatherResponse?,
        forecastData: ForecastWeatherResponse?
    ): CardWeatherData? {
        val currentWeatherItem = currentData?.data?.firstOrNull() ?: return null

        val currentTemp = currentWeatherItem.temp?.toInt() ?: 0

        var maxTemp = Int.MIN_VALUE
        var minTemp = Int.MAX_VALUE

        forecastData?.data?.forEach { forecast ->
            forecast.temp?.let { temp ->
                val tempInt = temp.toInt()
                if (tempInt > maxTemp) maxTemp = tempInt
                if (tempInt < minTemp) minTemp = tempInt
            }
        }

        // Caso não consiga obter máxima e mínima, usa a temperatura atual
        if (maxTemp == Int.MIN_VALUE) maxTemp = currentTemp
        if (minTemp == Int.MAX_VALUE) minTemp = currentTemp

        if (maxTemp < currentTemp) maxTemp = currentTemp
        if (minTemp > currentTemp) minTemp = currentTemp

        val uvIndex = currentWeatherItem.uv
        val airQuality = currentWeatherItem.aqi

        val hourlyWeatherData = forecastData?.data?.map { forecast ->
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

        val rainChance = forecastData?.data?.firstOrNull()?.pop?.toDouble()?.div(100)
            ?: (currentWeatherItem.precip?.toDouble()?.coerceAtMost(100.0)?.div(100) ?: 0.0)

        return CardWeatherData(
            cityName = currentWeatherItem.cityName ?: "Localização atual",
            countryCode = currentWeatherItem.countryCode ?: "",
            currentTemp = currentTemp,
            maxTemp = maxTemp,
            minTemp = minTemp,
            rainningChance = rainChance,
            status = currentWeatherItem.weather?.description ?: "Céu limpo",
            windSpeed = currentWeatherItem.windSpeed,
            humidity = currentWeatherItem.rh,
            pressure = currentWeatherItem.pres,
            windGustSpeed = currentWeatherItem.gust,
            solarRadiation = currentWeatherItem.solarRadiation,
            visibility = currentWeatherItem.vis?.toDouble(),
            uvIndex = uvIndex,
            airQuality = airQuality,
            hourlyWeatherData = hourlyWeatherData
        )
    }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    SearchBarComponent(
                        viewModel = viewModel,
                        modifier = Modifier.padding(vertical = 16.dp),
                        onDetailButtonClick = { cityName ->
                            // Esta funcionalidade pode ser implementada posteriormente
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Localização Atual",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        error != null -> {
                            Text(
                                text = "Erro: $error",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                        currentWeatherData != null && forecastWeatherData != null -> {
                            val weatherCardData = createWeatherCardData(currentWeatherData, forecastWeatherData)
                            if(weatherCardData == null) {
                                Text(
                                    text = "Ocorreu um erro ao procurar sua localização",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                WeatherCardComponent(
                                    weatherData = weatherCardData,
                                    modifier = Modifier,
                                    onCardClick = onCardClick
                                )
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = "Consultando sua localização...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 64.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Seção de Cidades Favoritas
            item {
                FavoriteCitiesSection(
                    favoriteWeatherData = favoriteWeatherData,
                    isLoading = isLoadingFavorites,
                    error = loadingError,
                    onCardClick = onCardClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}