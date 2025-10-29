package com.example.restaurantguide.data.dao

import androidx.room.*
import com.example.restaurantguide.data.model.Notice
import kotlinx.coroutines.flow.Flow

@Dao
interface NoticeDao {
    @Query("SELECT * FROM notices ORDER BY priority DESC, id DESC")
    fun getAll(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: Notice)

    @Query("DELETE FROM notices")
    suspend fun clear()
}
