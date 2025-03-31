package br.com.wheatherApp

import City
import MainViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.wheatherApp.data.api.getCurrentWeather
import br.com.wheatherApp.data.api.getHourlyForecastWeather
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.data.model.currentWeather.CurrentWeatherResponse
import br.com.wheatherApp.data.model.forecastWeather.ForecastWeatherResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object WeatherDetail : Screen("weather_detail/{weatherDataJson}")
    object CityWeatherDetail : Screen("city_weather_detail/{cityName}/{countryCode}")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    latitude: Double? = null,
    longitude: Double? = null,
    currentWeatherData: CurrentWeatherResponse? = null,
    forecastWeatherData: ForecastWeatherResponse? = null,
    isLoading: Boolean = false,
    error: String? = null
) {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            val application = LocalContext.current.applicationContext as Application
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(application))

            MainScreen(
                viewModel = viewModel,
                latitude = latitude,
                longitude = longitude,
                currentWeatherData = currentWeatherData,
                forecastWeatherData = forecastWeatherData,
                isLoading = isLoading,
                error = error,
                onCardClick = { weatherData ->
                    val weatherDataJson = json.encodeToString(weatherData)
                    val encodedJson = java.net.URLEncoder.encode(weatherDataJson, "UTF-8")
                    navController.navigate(Screen.WeatherDetail.route.replace("{weatherDataJson}", encodedJson))
                },
                onSearchCitySelected = { city ->
                    // Navega para os detalhes da cidade usando os parâmetros separados
                    navController.navigate("city_weather_detail/${city.cityName}/${city.countryCode}")
                }
            )
        }

        composable(Screen.WeatherDetail.route) { backStackEntry ->
            val weatherDataJson = backStackEntry.arguments?.getString("weatherDataJson") ?: ""
            val decodedJson = java.net.URLDecoder.decode(weatherDataJson, "UTF-8")
            val weatherData = json.decodeFromString<CardWeatherData>(decodedJson)

            val application = LocalContext.current.applicationContext as Application
            val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(application))

            WeatherDetailScreen(
                weatherData = weatherData,
                onBackClick = {
                    mainViewModel.refreshFavoriteCities()
                    navController.popBackStack()
                }
            )
        }

        // Nova rota para detalhes da cidade pesquisada por endereço
        composable(
            route = "city_weather_detail/{cityName}/{countryCode}",
            arguments = listOf(
                navArgument("cityName") { type = NavType.StringType },
                navArgument("countryCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val countryCode = backStackEntry.arguments?.getString("countryCode") ?: ""
            val application = LocalContext.current.applicationContext as Application
            val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(application))

            // Componente temporário para carregar os dados e então usar o WeatherDetailScreen
            CityWeatherLoader(
                cityName = cityName,
                countryCode = countryCode,
                onBackClick = {
                    mainViewModel.refreshFavoriteCities()
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Componente intermediário que carrega os dados da cidade e depois
 * renderiza o WeatherDetailScreen existente
 */
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

    // Carrega os dados do clima para a cidade
    LaunchedEffect(cityName, countryCode) {
        isLoading = true
        error = null

        coroutineScope.launch {
            try {
                val city = City(cityName, countryCode)

                // Carrega os dados em paralelo
                val currentResponse = getCurrentWeather(city, null)
                val forecastResponse = getHourlyForecastWeather(city, null)

                if (currentResponse != null && forecastResponse != null) {
                    // Converte para CardWeatherData
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

                        weatherData = CardWeatherData(
                            cityName = currentWeatherItem.cityName ?: cityName,
                            countryCode = currentWeatherItem.countryCode ?: countryCode,
                            currentTemp = currentTemp,
                            maxTemp = maxTemp,
                            minTemp = minTemp,
                            rainningChance = rainChance,
                            status = currentWeatherItem.weather?.description ?: "Céu limpo"
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

    // Exibe um indicador de carregamento enquanto os dados são buscados
    when {
        isLoading -> {
            LoadingScreen()
        }
        error != null -> {
            ErrorScreen(message = error ?: "Erro desconhecido", onRetry = {
                isLoading = true
                error = null
            })
        }
        weatherData != null -> {
            // Quando os dados estiverem prontos, use o WeatherDetailScreen existente
            WeatherDetailScreen(
                weatherData = weatherData!!,
                onBackClick = onBackClick
            )
        }
    }
}