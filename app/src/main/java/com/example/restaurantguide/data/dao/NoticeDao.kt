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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<Notice>)

    @Query("DELETE FROM notices")
    suspend fun clear()

    @Query("UPDATE notices SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: Long)

    @Query("UPDATE notices SET isRead = 1")
    suspend fun markAllRead()

    @Update
    suspend fun update(item: Notice)

}
