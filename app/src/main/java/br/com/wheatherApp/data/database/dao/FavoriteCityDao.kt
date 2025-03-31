package br.com.wheatherApp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCity(favoriteCity: FavoriteCity): Long

    @Query("SELECT * FROM favorite_cities ORDER BY timestamp DESC")
    fun getAllFavoriteCities(): Flow<List<FavoriteCity>>

    @Query("SELECT * FROM favorite_cities WHERE cityName = :cityName AND countryCode = :countryCode LIMIT 1")
    suspend fun getFavoriteCityByNameAndCountry(cityName: String, countryCode: String): FavoriteCity?

    @Query("DELETE FROM favorite_cities WHERE cityName = :cityName AND countryCode = :countryCode")
    suspend fun deleteFavoriteCity(cityName: String, countryCode: String): Int

    @Delete
    suspend fun deleteFavoriteCity(favoriteCity: FavoriteCity)
}