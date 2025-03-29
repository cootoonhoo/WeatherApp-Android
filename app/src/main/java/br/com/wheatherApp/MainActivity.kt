package br.com.wheatherApp

import SearchBarComponent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.wheatherApp.components.WeatherCardComponent
import br.com.wheatherApp.data.model.WeatherData
import br.com.wheatherApp.ui.theme.WheaterAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val weatherDataMock1 =  WeatherData(
                cityName = "São Paulo",
                currentTemp = 29,
                maxTemp = 31,
                minTemp = 21,
                ranningChance = 0.79,
                status = "Raining"
            )

        val weatherDataMock2 =  WeatherData(
            cityName = "Rio de Janeiro",
            currentTemp = 31,
            maxTemp = 34,
            minTemp = 29,
            ranningChance = 0.10,
            status = "Drizzle"
        )

        val weatherDataMock3 =  WeatherData(
            cityName = "Uberlandia",
            currentTemp = 23,
            maxTemp = 29,
            minTemp = 19,
            ranningChance = 0.40,
            status = "Lorem ipsum"
        )

        setContent {
            WheaterAppTheme {
                Scaffold { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        SearchBarComponent(modifier = Modifier.padding(top = 16.dp))
                        WeatherCardComponent(
                            weatherData = weatherDataMock1,
                            onCardClick = { clickedWeatherData ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Detalhes do clima para ${clickedWeatherData.cityName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                        WeatherCardComponent(
                            weatherData = weatherDataMock2,
                            onCardClick = { clickedWeatherData ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Detalhes do clima para ${clickedWeatherData.cityName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                        WeatherCardComponent(
                            weatherData = weatherDataMock3,
                            onCardClick = { clickedWeatherData ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Detalhes do clima para ${clickedWeatherData.cityName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}