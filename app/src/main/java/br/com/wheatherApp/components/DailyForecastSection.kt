package br.com.wheatherApp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.Divider
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
import br.com.wheatherApp.data.model.DailyWeatherData
import java.text.SimpleDateFormat
import java.util.Locale
/** Claude 3.7 Sonnet - Início
 * Crie uma segmento que demonstre a previsão de 7 dias. Essa informação pode ser obtida pela API
 * WeatherAPIService - getDailyForecastWeather()
 */

@Composable
fun DailyForecastSection(
    dailyForecasts: List<DailyWeatherData>?,
    modifier: Modifier = Modifier
) {
    if (dailyForecasts.isNullOrEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Previsão para 7 dias",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            dailyForecasts.forEach { dailyForecast ->
                DailyForecastRow(dailyForecast)

                if (dailyForecast != dailyForecasts.last()) {
                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DailyForecastRow(forecast: DailyWeatherData) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEE, dd/MM", Locale("pt", "BR"))

    val date = try {
        val parsedDate = inputFormat.parse(forecast.date)
        outputFormat.format(parsedDate)
    } catch (e: Exception) {
        forecast.date
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = date,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )

        forecast.description?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(1f)
            )
        }

        forecast.rainChance?.let { chance ->
            if (chance > 0.0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = "Chance de chuva",
                        tint = Color(0xFF5D9CEC),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${(chance * 100).toInt()}%",
                        fontSize = 14.sp,
                        color = Color(0xFF5D9CEC)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${forecast.maxTemp}°",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE57373)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${forecast.minTemp}°",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5D9CEC)
            )
        }
    }
}
/** Claude 3.7 Sonnet - Fim */