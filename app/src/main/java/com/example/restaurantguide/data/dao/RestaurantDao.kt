package com.example.restaurantguide.data.dao

import androidx.room.*
import com.example.restaurantguide.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants ORDER BY rating DESC")
    fun getAll(): Flow<List<Restaurant>>

    @Query("SELECT * FROM restaurants WHERE cuisine = :cuisine ORDER BY rating DESC")
    fun getByCuisine(cuisine: String): Flow<List<Restaurant>>

    @Query("SELECT * FROM restaurants WHERE isFavorite = 1 ORDER BY rating DESC")
    fun getFavorites(): Flow<List<Restaurant>>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    suspend fun getById(id: Long): Restaurant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: Restaurant)

    @Update suspend fun update(item: Restaurant)
    @Delete suspend fun delete(item: Restaurant)

    @Query("DELETE FROM restaurants")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM restaurants")
    suspend fun count(): Int

}
