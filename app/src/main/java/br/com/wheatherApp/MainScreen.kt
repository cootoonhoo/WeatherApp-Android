package br.com.wheatherApp

import City
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.wheatherApp.components.CurrentLocationWeatherComponent
import br.com.wheatherApp.components.FavoriteCitiesSection
import br.com.wheatherApp.components.WeatherCardComponent
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.data.model.DailyWeatherData
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
    dailyForecastWeatherData: ForecastWeatherResponse? = null, // Novo parâmetro para previsão diária
    isLoading: Boolean = false,
    error: String? = null,
    onCardClick: (CardWeatherData) -> Unit,
    onSearchCitySelected: (City) -> Unit
) {
    fun createWeatherCardData(
        currentData: CurrentWeatherResponse?,
        forecastData: ForecastWeatherResponse?,
        dailyForecastData: ForecastWeatherResponse? = null
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

        val dailyWeatherData = dailyForecastData?.data?.map { dailyForecast ->
            DailyWeatherData(
                date = dailyForecast.validDate ?: "N/A",
                maxTemp = dailyForecast.maxTemp?.toInt() ?: currentTemp,
                minTemp = dailyForecast.minTemp?.toInt() ?: currentTemp,
                description = dailyForecast.weather?.description,
                rainChance = dailyForecast.pop?.toDouble()?.div(100)
            )
        }

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
            hourlyWeatherData = hourlyWeatherData,
            dailyWeatherData = dailyWeatherData
        )
    }

    val favoriteWeatherData by viewModel.favoriteWeatherData.collectAsState()
    val isLoadingFavorites by viewModel.isLoadingFavorites.collectAsState()
    val loadingError by viewModel.loadingError.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBarComponent(
                viewModel = viewModel,
                onDetailButtonClick = { city ->
                    onSearchCitySelected(city)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            CurrentLocationWeatherComponent(
                isLoading = isLoading,
                error = error,
                currentWeatherData = currentWeatherData,
                forecastWeatherData = forecastWeatherData,
                createWeatherCardData = { current, forecast ->
                    createWeatherCardData(current, forecast, dailyForecastWeatherData)
                },
                onCardClick = onCardClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            FavoriteCitiesSection(
                favoriteWeatherData = favoriteWeatherData,
                isLoading = isLoadingFavorites,
                error = loadingError,
                onCardClick = onCardClick
            )
        }
    }
}