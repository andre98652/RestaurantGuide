package com.example.restaurantguide.data.model

data class UserProfile(
    val id: Long = 0,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)
