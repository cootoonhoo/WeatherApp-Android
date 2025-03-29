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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Exemplo de JSON de dados meteorológicos
        val weatherJson = """
            { 
                "cityName": "São Paulo", 
                "currentTemp": 29, 
                "maxTemp": 31, 
                "minTemp": 21, 
                "ranningChance": 0.79, 
                "status": "Raining" 
            }
        """

        val weatherData = try {
            Json.decodeFromString<WeatherData>(weatherJson)
        } catch (e: Exception) {
            WeatherData(
                cityName = "São Paulo",
                currentTemp = 29,
                maxTemp = 31,
                minTemp = 21,
                ranningChance = 0.79,
                status = "Raining"
            )
        }

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
                            weatherData = weatherData,
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