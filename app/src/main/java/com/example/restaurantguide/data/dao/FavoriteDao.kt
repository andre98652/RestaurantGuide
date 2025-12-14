package com.example.restaurantguide.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.restaurantguide.data.model.Favorite
import com.example.restaurantguide.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId")
    fun getFavoritesForUser(userId: Long): Flow<List<Favorite>>

    @Query("SELECT r.* FROM restaurants r INNER JOIN favorites f ON r.id = f.restaurantId WHERE f.userId = :userId")
    fun getFavoriteRestaurantsForUser(userId: Long): Flow<List<Restaurant>>

    @Query("SELECT COUNT(*) FROM favorites WHERE userId = :userId AND restaurantId = :restaurantId")
    suspend fun isFavorite(userId: Long, restaurantId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<Favorite>)

    @Query("DELETE FROM favorites")
    suspend fun clearAll()

    @Query("DELETE FROM favorites WHERE userId = :userId AND restaurantId = :restaurantId")
    suspend fun removeFavorite(userId: Long, restaurantId: Long)

    @Query("SELECT COUNT(*) FROM favorites WHERE restaurantId = :restaurantId")
    suspend fun getFavoriteCount(restaurantId: Long): Int
}
