package com.example.restaurantguide.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.restaurantguide.data.dao.NoticeDao
import com.example.restaurantguide.data.dao.RestaurantDao
import com.example.restaurantguide.data.model.Notice
import com.example.restaurantguide.data.model.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Restaurant::class, Notice::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun noticeDao(): NoticeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "restaurant.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { db ->
                        INSTANCE = db
                        // Seed de ejemplo (opcional)
                        CoroutineScope(Dispatchers.IO).launch {
                            // Inserta algunos restaurantes si la tabla está vacía
                            // (puedes hacerlo luego desde un Repository también)
                        }
                    }
            }
    }
}
