package com.example.restaurantguide.repository

import com.example.restaurantguide.data.dao.RestaurantDao
import com.example.restaurantguide.data.model.Restaurant
import kotlinx.coroutines.flow.Flow

class RestaurantRepository(private val dao: RestaurantDao) {
    fun all(): Flow<List<Restaurant>> = dao.getAll()
    fun byCuisine(cuisine: String): Flow<List<Restaurant>> = dao.getByCuisine(cuisine)
    fun favorites(): Flow<List<Restaurant>> = dao.getFavorites()
    suspend fun upsert(item: Restaurant) = dao.upsert(item)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun toggleFavorite(id: Long) {
        val current = dao.getById(id) ?: return
        dao.update(current.copy(isFavorite = !current.isFavorite))
    }
}
