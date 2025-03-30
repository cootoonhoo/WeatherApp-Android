package br.com.wheatherApp

import MainViewModel
import SearchBarComponent
import br.com.wheatherApp.data.api.getCurrentWeather
import br.com.wheatherApp.data.api.getHourlyForecastWeather
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.wheatherApp.components.WeatherCardComponent
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
    private val isLoading = mutableStateOf(false)
    private val weatherError = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissions()

        setContent {
            WheaterAppTheme {
                WeatherApp(
                    latitude = latitude.value,
                    longitude = longitude.value,
                    currentWeatherData = currentWeather.value,
                    forecastWeatherData = forecastWeather.value,
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

                CoroutineScope(Dispatchers.Main).launch {
                    currentWeather.value = currentResponse
                    forecastWeather.value = forecastResponse
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

    @Composable
    fun WeatherApp(
        viewModel: MainViewModel = viewModel(),
        latitude: Double? = null,
        longitude: Double? = null,
        currentWeatherData: CurrentWeatherResponse? = null,
        forecastWeatherData: ForecastWeatherResponse? = null,
        isLoading: Boolean = false,
        error: String? = null
    ) {
        fun createWeatherCardData(
            currentData: CurrentWeatherResponse?,
            forecastData: ForecastWeatherResponse?
        ): CardWeatherData? {
            val currentWeatherItem = currentData?.data?.firstOrNull() ?: return null

            val currentTemp = currentWeatherItem.temp?.toInt() ?: 0

            var maxTemp = Int.MIN_VALUE
            var minTemp = Int.MAX_VALUE

            forecastData?.data?.forEach { forecast ->
                forecast.temp?.let { temp ->
                    val tempInt = temp.toInt()
                    if (tempInt > maxTemp) maxTemp = tempInt
                    if (tempInt < minTemp) minTemp = tempInt
                }
            }

            val rainChance = forecastData?.data?.firstOrNull()?.pop?.toDouble()?.div(100)
                ?: (currentWeatherItem.precip?.toDouble()?.coerceAtMost(100.0)?.div(100) ?: 0.0)

            return CardWeatherData(
                cityName = currentWeatherItem.cityName ?: "Localização atual",
                currentTemp = currentTemp,
                maxTemp = maxTemp,
                minTemp = minTemp,
                rainningChance = rainChance,
                status = currentWeatherItem.weather?.description ?: "Céu limpo"
            )
        }
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    SearchBarComponent(
                        modifier = Modifier.padding(top = 16.dp),
                        onDetailButtonClick = { cityName ->
                            Toast.makeText(
                                this@MainActivity,
                                "Detalhes do clima para $cityName",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Localização Atual",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    when {
                        isLoading -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Carregando dados meteorológicos...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                        error != null -> {
                            Text(
                                text = "Erro: $error",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        currentWeatherData != null && forecastWeatherData != null -> {
                            val weatherCardData = createWeatherCardData(currentWeatherData,forecastWeatherData)
                            if(weatherCardData == null)
                            {
                                Text(
                                    text = "Ocorreu um erro ao procurar sua localização",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            else
                                WeatherCardComponent(weatherCardData, modifier = Modifier, onCardClick = {
                            })
                        }
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Consultando sua localização...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Current Location
                }
            }
        }
    }
}