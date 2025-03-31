import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.app.Application
import android.util.Log
import br.com.wheatherApp.data.api.getCurrentWeather
import br.com.wheatherApp.data.api.getDailyForecastWeather
import br.com.wheatherApp.data.api.getHourlyForecastWeather
import br.com.wheatherApp.data.database.FavoriteCity
import br.com.wheatherApp.data.database.WeatherDatabase
import br.com.wheatherApp.data.model.CardWeatherData
import br.com.wheatherApp.data.model.DailyWeatherData
import br.com.wheatherApp.data.model.HourlyWeatherData
import br.com.wheatherApp.data.model.currentWeather.CurrentWeatherResponse
import br.com.wheatherApp.data.model.forecastWeather.ForecastWeatherResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val application: Application): ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _favoriteCities = MutableStateFlow<List<FavoriteCity>>(emptyList())
    val favoriteCities = _favoriteCities.asStateFlow()

    private val _favoriteWeatherData = MutableStateFlow<List<CardWeatherData>>(emptyList())
    val favoriteWeatherData = _favoriteWeatherData.asStateFlow()

    private val _isLoadingFavorites = MutableStateFlow(false)
    val isLoadingFavorites = _isLoadingFavorites.asStateFlow()

    private val _loadingError = MutableStateFlow<String?>(null)
    val loadingError = _loadingError.asStateFlow()

    // Changed from MOCK_ListOfCities to an empty list that will be populated from database
    private val _listOfCities = MutableStateFlow<List<City>>(emptyList())

    @OptIn(FlowPreview::class)
    val listOfCities = searchText
        .debounce(700L)
        .onEach { _isSearching.update { true } }
        .combine(_listOfCities){ text, cities ->
            if(text.isBlank()) {
                cities
            } else {
                cities.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _listOfCities.value
        )

    init {
        loadFavoriteCities()
        // Also convert favorite cities to City objects for search
        updateCitiesListFromFavorites()
    }

    // New function to convert FavoriteCity objects to City objects
    private fun updateCitiesListFromFavorites() {
        viewModelScope.launch {
            try {
                val database = WeatherDatabase.getDatabase(application)
                val dao = database.favoriteCityDao()

                dao.getAllFavoriteCities().collect { favorites ->
                    // Convert FavoriteCity objects to City objects for the search list
                    val cities = favorites.map { favoriteCity ->
                        City(favoriteCity.cityName, favoriteCity.countryCode)
                    }
                    _listOfCities.value = cities
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error converting favorites to cities: ${e.message}")
            }
        }
    }

    // Adds a new city to the list of cities
    fun addCityToList(city: City) {
        val currentList = _listOfCities.value.toMutableList()
        // Check if the city already exists in the list to avoid duplicates
        if (!currentList.any { it.cityName == city.cityName && it.countryCode == city.countryCode }) {
            currentList.add(city)
            _listOfCities.value = currentList
        }
    }

    fun refreshFavoriteCities() {
        loadFavoriteCities()
    }

    private fun loadFavoriteCities() {
        viewModelScope.launch {
            try {
                val database = WeatherDatabase.getDatabase(application)
                val dao = database.favoriteCityDao()

                dao.getAllFavoriteCities().collect { cities ->
                    _favoriteCities.value = cities
                    loadWeatherForFavoriteCities(cities)
                    // Update the search list when favorites change
                    updateCitiesListFromFavorites()
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading favorite cities: ${e.message}")
                _loadingError.value = "Erro ao carregar cidades favoritas: ${e.message}"
            }
        }
    }

    private fun loadWeatherForFavoriteCities(cities: List<FavoriteCity>) {
        if (cities.isEmpty()) {
            _favoriteWeatherData.value = emptyList()
            return
        }

        _isLoadingFavorites.value = true
        _loadingError.value = null

        viewModelScope.launch {
            try {
                val weatherDataList = mutableListOf<CardWeatherData>()

                cities.forEach { city ->
                    try {
                        val cityObj = City(city.cityName, city.countryCode)
                        val currentResponse = getCurrentWeather(cityObj, null)
                        val forecastResponse = getHourlyForecastWeather(cityObj, null)
                        val dailyForecastResponse = getDailyForecastWeather(cityObj, null)

                        if (currentResponse != null && forecastResponse != null) {
                            val weatherData = createWeatherCardData(currentResponse, forecastResponse, dailyForecastResponse)
                            weatherData?.let { weatherDataList.add(it) }
                        }
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "Error loading weather for ${city.cityName}: ${e.message}")
                    }
                }

                _favoriteWeatherData.value = weatherDataList
                _isLoadingFavorites.value = false
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading weather for favorite cities: ${e.message}")
                _loadingError.value = "Erro ao carregar dados do clima: ${e.message}"
                _isLoadingFavorites.value = false
            }
        }
    }


    fun createWeatherCardData(
        currentData: CurrentWeatherResponse?,
        forecastData: ForecastWeatherResponse?,
        dailyForecastData: ForecastWeatherResponse? = null
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

        // Caso não consiga obter máxima e mínima, usa a temperatura atual
        if (maxTemp == Int.MIN_VALUE) maxTemp = currentTemp
        if (minTemp == Int.MAX_VALUE) minTemp = currentTemp

        if (maxTemp < currentTemp) maxTemp = currentTemp
        if (minTemp > currentTemp) minTemp = currentTemp

        val uvIndex = currentWeatherItem.uv
        val airQuality = currentWeatherItem.aqi

        val hourlyWeatherData = forecastData?.data?.map { forecast ->
            HourlyWeatherData(
                temp = forecast.temp,
                time = forecast.datetime ?: "N/A",
                humidity = forecast.rh,
                windSpeed = forecast.windSpeed,
                rainChance = forecast.pop?.toDouble(),
                uvIndex = forecast.uv,
                airQuality = airQuality
            )
        } ?: emptyList()

        // Processar previsão diária se disponível
        val dailyWeatherData = dailyForecastData?.data?.map { dailyForecast ->
            DailyWeatherData(
                date = dailyForecast.validDate ?: "N/A",
                maxTemp = dailyForecast.maxTemp?.toInt() ?: currentTemp,
                minTemp = dailyForecast.minTemp?.toInt() ?: currentTemp,
                description = dailyForecast.weather?.description,
                rainChance = dailyForecast.pop?.toDouble()?.div(100)
            )
        }

        val rainChance = forecastData?.data?.firstOrNull()?.pop?.toDouble()?.div(100)
            ?: (currentWeatherItem.precip?.toDouble()?.coerceAtMost(100.0)?.div(100) ?: 0.0)

        return CardWeatherData(
            cityName = currentWeatherItem.cityName ?: "Localização Atual",
            countryCode = currentWeatherItem.countryCode ?: "",
            currentTemp = currentTemp,
            maxTemp = maxTemp,
            minTemp = minTemp,
            rainningChance = rainChance,
            status = currentWeatherItem.weather?.description ?: "Céu limpo",
            windSpeed = currentWeatherItem.windSpeed,
            humidity = currentWeatherItem.rh,
            pressure = currentWeatherItem.pres,
            windGustSpeed = currentWeatherItem.gust,
            solarRadiation = currentWeatherItem.solarRadiation,
            visibility = currentWeatherItem.vis?.toDouble(),
            uvIndex = uvIndex,
            airQuality = airQuality,
            hourlyWeatherData = hourlyWeatherData,
            dailyWeatherData = dailyWeatherData
        )
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onCitySelected(city: City) {
        Log.d("CitySelection", "Cidade selecionada: ${city.cityName}")
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}