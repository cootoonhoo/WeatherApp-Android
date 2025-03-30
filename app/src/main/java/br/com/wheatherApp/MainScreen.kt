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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.wheatherApp.components.WeatherCardComponent
import br.com.wheatherApp.data.model.CardWeatherData
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

        val rainChance = forecastData?.data?.firstOrNull()?.pop?.toDouble()?.div(100)
            ?: (currentWeatherItem.precip?.toDouble()?.coerceAtMost(100.0)?.div(100) ?: 0.0)

        return CardWeatherData(
            cityName = currentWeatherItem.cityName?: "Localização atual",
            countryCode = currentWeatherItem.countryCode?: "",
            currentTemp = currentTemp,
            maxTemp = maxTemp,
            minTemp = minTemp,
            rainningChance = rainChance,
            status = currentWeatherItem.weather?.description ?: "Céu limpo"
        )
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SearchBarComponent(
                    modifier = Modifier.padding(top = 16.dp),
                    onDetailButtonClick = { cityName ->
                        // Esta funcionalidade pode ser implementada posteriormente
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Localização Atual",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Carregando dados meteorológicos...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                    error != null -> {
                        Text(
                            text = "Erro: $error",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    currentWeatherData != null && forecastWeatherData != null -> {
                        val weatherCardData = createWeatherCardData(currentWeatherData, forecastWeatherData)
                        if(weatherCardData == null) {
                            Text(
                                text = "Ocorreu um erro ao procurar sua localização",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        } else {
                            WeatherCardComponent(
                                weatherData = weatherCardData,
                                modifier = Modifier,
                                onCardClick = onCardClick
                            )
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Consultando sua localização...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}