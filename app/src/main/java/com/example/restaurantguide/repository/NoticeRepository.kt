package com.example.restaurantguide.repository

import com.example.restaurantguide.data.model.Notice
import com.example.restaurantguide.data.network.FirestoreService
import com.example.restaurantguide.data.prefs.UserPreferences
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class NoticeRepository(db: com.example.restaurantguide.data.database.AppDatabase) {
    private val firestore = FirestoreService()
    private val cloudDb = FirebaseFirestore.getInstance()
    private val localDao = db.noticeDao()

    // 1. LEER DE LOCAL (Offline First)
    fun all(): Flow<List<Notice>> = localDao.getAll()

    // 2. SYNC (Cloud -> Local)
    init {
       cloudDb.collection("notices").addSnapshotListener { snap, _ ->
           if (snap != null) {
               val list = snap.toObjects(Notice::class.java)
               kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                   // Upsert all notices
                   localDao.upsertAll(list)
                   
                   // (Opcional) Limpieza de antiguos
                   // localDao.deleteExpired(...) 
               }
           }
       }
    }

    // Guarda un aviso nuevo (promoción)
    suspend fun upsert(notice: Notice) {
        firestore.saveNotice(notice)
        // La nube disparará el listener y actualizará local
    }

    suspend fun markRead(id: Long) {
        localDao.markRead(id)
    }

    suspend fun markAllRead() {
        localDao.markAllRead()
    }
}
