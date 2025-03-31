package br.com.wheatherApp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.wheatherApp.data.model.CardWeatherData

@Composable
fun FavoriteCitiesSection(
    favoriteWeatherData: List<CardWeatherData>,
    isLoading: Boolean,
    error: String?,
    onCardClick: (CardWeatherData) -> Unit
) {
    fun formatWeatherData( weatherData: CardWeatherData  ) {
        val currentTemp = weatherData.currentTemp

        if (currentTemp < weatherData.minTemp) weatherData.minTemp = currentTemp
        if (currentTemp > weatherData.maxTemp) weatherData.maxTemp = currentTemp
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Cidades Favoritas",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Carregando cidades favoritas...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            error != null -> {
                Text(
                    text = "Erro ao carregar favoritos: $error",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            favoriteWeatherData.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Você ainda não adicionou cidades aos favoritos",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Para adicionar, pesquise uma cidade e clique no ícone de favorito na tela de detalhes",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                favoriteWeatherData.forEach { weatherData ->
                    formatWeatherData(weatherData)
                    WeatherCardComponent(
                        weatherData = weatherData,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        onCardClick = onCardClick
                    )
                }
            }
        }
    }
}