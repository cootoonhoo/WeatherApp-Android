package br.com.wheatherApp

import City
import ErrorScreen
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
import br.com.wheatherApp.data.api.getDailyForecastWeather
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.data.model.currentWeather.CurrentWeatherResponse
import br.com.wheatherApp.data.model.forecastWeather.ForecastWeatherResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import br.com.wheatherApp.components.CityWeatherLoader
import br.com.wheatherApp.data.model.HourlyWeatherData
import kotlinx.coroutines.launch

/** Claude 3.7 Sonnet - Início
 * Crie um sistema de navegação entre as telas do projeto.
 */

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
    dailyForecastWeatherData: ForecastWeatherResponse? = null, // Novo parâmetro para previsão diária
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
                dailyForecastWeatherData = dailyForecastWeatherData,
                isLoading = isLoading,
                error = error,
                onCardClick = { weatherData ->
                    val weatherDataJson = json.encodeToString(weatherData)
                    val encodedJson = java.net.URLEncoder.encode(weatherDataJson, "UTF-8")
                    navController.navigate(Screen.WeatherDetail.route.replace("{weatherDataJson}", encodedJson))
                },
                onSearchCitySelected = { city ->
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

/** Claude 3.7 Sonnet - Fim */