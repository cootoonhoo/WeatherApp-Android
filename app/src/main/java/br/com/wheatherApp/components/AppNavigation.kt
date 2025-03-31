package br.com.wheatherApp

import MainViewModel
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.data.model.currentWeather.CurrentWeatherResponse
import br.com.wheatherApp.data.model.forecastWeather.ForecastWeatherResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.app.Application
import androidx.compose.ui.platform.LocalContext

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
    }
}