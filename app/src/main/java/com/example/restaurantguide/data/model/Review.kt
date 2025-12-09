package com.example.restaurantguide.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 0,
    val userName: String = "", // Denormalized for simpler UI
    val restaurantId: Long = 0,
    val comment: String = "",
    val rating: Int = 0 // 1..5
)
