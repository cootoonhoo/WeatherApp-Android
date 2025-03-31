package br.com.wheatherApp.data.repository

import br.com.wheatherApp.data.database.FavoriteCity
import br.com.wheatherApp.data.database.FavoriteCityDao
import kotlinx.coroutines.flow.Flow

class FavoriteCityRepository(private val favoriteCityDao: FavoriteCityDao) {

    val allFavoriteCities: Flow<List<FavoriteCity>> = favoriteCityDao.getAllFavoriteCities()

    suspend fun insertFavoriteCity(cityName: String, countryCode: String): Long {
        val favoriteCity = FavoriteCity(
            cityName = cityName,
            countryCode = countryCode
        )
        return favoriteCityDao.insertFavoriteCity(favoriteCity)
    }

    suspend fun isCityFavorite(cityName: String, countryCode: String): Boolean {
        return favoriteCityDao.getFavoriteCityByNameAndCountry(cityName, countryCode) != null
    }

    suspend fun deleteFavoriteCity(cityName: String, countryCode: String): Int {
        return favoriteCityDao.deleteFavoriteCity(cityName, countryCode)
    }
}