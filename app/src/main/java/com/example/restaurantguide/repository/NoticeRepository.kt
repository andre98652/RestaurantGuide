package com.example.restaurantguide.repository

import com.example.restaurantguide.data.dao.NoticeDao
import com.example.restaurantguide.data.model.Notice
import kotlinx.coroutines.flow.Flow

class NoticeRepository(private val dao: NoticeDao) {
    fun all(): Flow<List<Notice>> = dao.getAll()
    suspend fun upsert(item: Notice) = dao.upsert(item)
    suspend fun markRead(id: Long) = dao.markRead(id)
    suspend fun markAllRead() = dao.markAllRead()
}

