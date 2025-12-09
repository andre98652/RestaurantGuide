package com.example.restaurantguide.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class Restaurant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val cuisine: String = "",       // Peruana, Italiana, Japonesa, Parrillas…
    val priceLevel: Int = 1,       // 1..4 (equiv a $, $$, $$$, $$$$)
    val address: String = "",
    val schedule: String = "",      // "13:00 - 23:30"
    val rating: Double = 0.0,        // 0.0..5.0
    val description: String = "",
    val imageUrls: String = "",// CSV simple: "res1;res2;res3"
    val isFavorite: Boolean = false,
    val ownerId: Long? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
