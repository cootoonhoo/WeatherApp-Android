package br.com.wheatherApp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.wheatherApp.data.database.FavoriteCity
import br.com.wheatherApp.data.database.WeatherDatabase
import br.com.wheatherApp.data.repository.FavoriteCityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoriteCityRepository

    private val _favoriteCities = MutableStateFlow<List<FavoriteCity>>(emptyList())
    val favoriteCities: StateFlow<List<FavoriteCity>> = _favoriteCities.asStateFlow()

    private val _isCityFavorite = MutableStateFlow<Boolean>(false)
    val isCityFavorite: StateFlow<Boolean> = _isCityFavorite.asStateFlow()

    init {
        val database = WeatherDatabase.getDatabase(application)
        val dao = database.favoriteCityDao()
        repository = FavoriteCityRepository(dao)

        viewModelScope.launch {
            repository.allFavoriteCities.collect { cities ->
                _favoriteCities.value = cities
            }
        }
    }

    fun checkIfCityIsFavorite(cityName: String, countryCode: String) {
        viewModelScope.launch {
            _isCityFavorite.value = repository.isCityFavorite(cityName, countryCode)
        }
    }

    fun toggleFavorite(cityName: String, countryCode: String) {
        viewModelScope.launch {
            if (_isCityFavorite.value) {
                repository.deleteFavoriteCity(cityName, countryCode)
            } else {
                repository.insertFavoriteCity(cityName, countryCode)
            }
            _isCityFavorite.value = !_isCityFavorite.value
        }
    }
}