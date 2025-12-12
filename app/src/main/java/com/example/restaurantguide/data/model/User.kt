package com.example.restaurantguide.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val password: String, // Texto plano para esta demo (hash real en producción)
    val role: String, // "USER" or "RESTAURANT"
    val avatarUrl: String? = null
)
