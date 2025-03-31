package br.com.wheatherApp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.wheatherApp.components.RainEffect
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.ui.viewmodels.FavoriteViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailScreen(
    weatherData: CardWeatherData,
    onBackClick: () -> Unit,
    favoriteViewModel: FavoriteViewModel = viewModel()
) {
    // Verificar se a cidade já está nos favoritos
    LaunchedEffect(weatherData) {
        favoriteViewModel.checkIfCityIsFavorite(weatherData.cityName, weatherData.countryCode)
    }

    val isFavorite by favoriteViewModel.isCityFavorite.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Verificar se está chovendo para mostrar o efeito de chuva
    val isRaining = weatherData.status.contains("Rain", ignoreCase = true) ||
            weatherData.rainningChance > 0.5

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "${weatherData.cityName}, ${weatherData.countryCode}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    // Botão de bookmark com estado real do banco de dados
                    IconButton(
                        onClick = {
                            favoriteViewModel.toggleFavorite(weatherData.cityName, weatherData.countryCode)

                            // Exibir um Snackbar informando ao usuário
                            scope.launch {
                                val message = if (isFavorite) {
                                    "${weatherData.cityName} removida dos favoritos"
                                } else {
                                    "${weatherData.cityName} adicionada aos favoritos"
                                }
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                            tint = if (isFavorite) Color(0xFF5D9CEC) else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Efeito de chuva se aplicável
            if (isRaining) {
                RainEffect(
                    modifier = Modifier.fillMaxSize(),
                    density = if (weatherData.rainningChance > 0.8) 0.8f else 0.5f,
                    speed = 1.8f,
                    color = Color(0x366DA8F1)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Card principal com detalhes do clima
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = weatherData.cityName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = weatherData.countryCode,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "${weatherData.currentTemp}°C",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = weatherData.status,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Máx: ${weatherData.maxTemp}°C",
                                fontSize = 18.sp,
                                color = Color(0xFFE57373),
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "Mín: ${weatherData.minTemp}°C",
                                fontSize = 18.sp,
                                color = Color(0xFF5D9CEC),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (!isRaining) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WaterDrop,
                                    contentDescription = "Chance de chuva",
                                    tint = Color(0xFF5D9CEC).copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "Chance de chuva: ${(weatherData.rainningChance * 100).toInt()}%",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão para favoritar/desfavoritar
                Button(
                    onClick = {
                        favoriteViewModel.toggleFavorite(weatherData.cityName, weatherData.countryCode)

                        scope.launch {
                            val message = if (isFavorite) {
                                "${weatherData.cityName} removida dos favoritos"
                            } else {
                                "${weatherData.cityName} adicionada aos favoritos"
                            }
                            snackbarHostState.showSnackbar(message)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isFavorite)
                            "Remover ${weatherData.cityName} dos favoritos"
                        else
                            "Adicionar ${weatherData.cityName} aos favoritos"
                    )
                }
            }
        }
    }
}