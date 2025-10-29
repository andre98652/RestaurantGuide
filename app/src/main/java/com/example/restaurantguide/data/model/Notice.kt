package com.example.restaurantguide.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val restaurantId: Long?,         // para abrir detalle
    val type: String,                // "PROMO", "NEWS", "EVENT"
    val title: String,
    val summary: String,
    val startAt: Long? = null,
    val endAt: Long? = null,
    val city: String? = null,
    val priority: Int = 0,
    val isRead: Boolean = false
)
