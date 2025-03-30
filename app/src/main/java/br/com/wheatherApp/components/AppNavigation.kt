package br.com.wheatherApp

import MainViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.wheatherApp.data.model.CardWeatherData
import kotlinx.serialization.decodeFromString
import br.com.wheatherApp.data.model.currentWeather.CurrentWeatherResponse
import br.com.wheatherApp.data.model.forecastWeather.ForecastWeatherResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object WeatherDetail : Screen("weather_detail/{weatherDataJson}")
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
            val viewModel: MainViewModel = viewModel()

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
                }
            )
        }

        composable(Screen.WeatherDetail.route) { backStackEntry ->
            val weatherDataJson = backStackEntry.arguments?.getString("weatherDataJson") ?: ""
            val decodedJson = java.net.URLDecoder.decode(weatherDataJson, "UTF-8")
            val weatherData = json.decodeFromString<CardWeatherData>(decodedJson)

            WeatherDetailScreen(
                weatherData = weatherData,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}