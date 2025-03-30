package br.com.wheatherApp.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.wheatherApp.data.model.CardWeatherData

@Composable
fun WeatherCardComponent(
    weatherData: CardWeatherData,
    modifier: Modifier = Modifier,
    onCardClick: (CardWeatherData) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onCardClick(weatherData) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        /** Claude 3.7 Sonnet - Início
         * Faça que quando esteja chovendo, o WeaherCardComponent tenha um efeito de chuva
         */
        Box(modifier = Modifier.fillMaxWidth()) {
            val isRaining = weatherData.status.contains("Rain", ignoreCase = true) ||
                    weatherData.rainningChance > 0.5

            if (isRaining) {
                RainEffect(
                    modifier = Modifier.matchParentSize(),
                    density = if (weatherData.rainningChance > 0.8) 0.8f else 0.5f,
                    speed = 1.8f,
                    color = Color(0x366DA8F1)
                )
            }
            /** Claude 3.7 Sonnet - Fim */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Cidade + chances de chuva
                Row (verticalAlignment = Alignment.CenterVertically) {
                    val rainningChanceColor =  Color(0x65FFFFFF)
                    Text(
                        text = "${weatherData.cityName},${weatherData.countryCode}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    if(!isRaining)
                    {
                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Filled.WaterDrop,
                            contentDescription = "Chance de chuva",
                            tint = rainningChanceColor,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = "${(weatherData.rainningChance * 100).toInt()}%",
                            color = rainningChanceColor,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Temperatura máxima e mínima
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${weatherData.currentTemp}°C",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row {
                            Text(
                                text = "${weatherData.maxTemp}°C",
                                fontSize = 14.sp,
                                color = Color(0xFFE57373),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row {
                            Text(
                                text = "${weatherData.minTemp}°C",
                                fontSize = 14.sp,
                                color = Color(0xFF5D9CEC),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Barrinha indicando o clima
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            when {
                                weatherData.status.contains("Rain", ignoreCase = true) ||
                                        weatherData.rainningChance > 0.5 -> Color(0xFF6DA8F1)
                                weatherData.currentTemp > 30 -> Color(0xFFFF9C7D)
                                weatherData.currentTemp < 15 -> Color(0xFF636EAD)
                                else -> Color(0xFF81C784)
                            }
                        )
                )
            }
        }
    }
}