package br.com.wheatherApp

import MainViewModel
import SearchBarComponent
import br.com.wheatherApp.data.api.getCurrentWeather
import br.com.wheatherApp.data.api.getHourlyForecastWeather
import br.com.wheatherApp.data.api.getDailyForecastWeather
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.data.model.Location
import br.com.wheatherApp.data.model.currentWeather.CurrentWeatherResponse
import br.com.wheatherApp.data.model.forecastWeather.ForecastWeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import br.com.wheatherApp.ui.theme.WheaterAppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Permission granted, get location
                getLastLocation()
            }
            else -> {
                // No location permission granted
                Toast.makeText(
                    this,
                    "Permissão de localização negada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val latitude = mutableStateOf<Double?>(null)
    private val longitude = mutableStateOf<Double?>(null)
    private val currentWeather = mutableStateOf<CurrentWeatherResponse?>(null)
    private val forecastWeather = mutableStateOf<ForecastWeatherResponse?>(null)
    private val dailyForecastWeather = mutableStateOf<ForecastWeatherResponse?>(null) // Novo estado para previsão diária
    private val isLoading = mutableStateOf(false)
    private val weatherError = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissions()

        setContent {
            WheaterAppTheme {
                AppNavigation(
                    latitude = latitude.value,
                    longitude = longitude.value,
                    currentWeatherData = currentWeather.value,
                    forecastWeatherData = forecastWeather.value,
                    dailyForecastWeatherData = dailyForecastWeather.value, // Passe a previsão diária para a navegação
                    isLoading = isLoading.value,
                    error = weatherError.value
                )
            }
        }
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                getLastLocation()
            }
            else -> {
                // Request permissions
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    // Update location state
                    latitude.value = it.latitude
                    longitude.value = it.longitude
                    fetchCurrentWeatherForLocation(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Falha ao obter localização: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun fetchCurrentWeatherForLocation(lat: Double, lon: Double) {
        isLoading.value = true
        weatherError.value = null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val locationObj = Location(lat, lon)
                val currentResponse = getCurrentWeather(null, locationObj)
                val forecastResponse = getHourlyForecastWeather(null, locationObj)
                val dailyResponse = getDailyForecastWeather(null, locationObj)

                CoroutineScope(Dispatchers.Main).launch {
                    currentWeather.value = currentResponse
                    forecastWeather.value = forecastResponse
                    dailyForecastWeather.value = dailyResponse
                    isLoading.value = false
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    weatherError.value = e.message ?: "Erro desconhecido ao buscar dados meteorológicos"
                    isLoading.value = false
                }
            }
        }
    }
}