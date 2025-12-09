package com.example.restaurantguide.repository

import com.example.restaurantguide.data.model.Notice
import com.example.restaurantguide.data.network.FirestoreService
import com.example.restaurantguide.data.prefs.UserPreferences
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NoticeRepository {
    private val firestore = FirestoreService()
    private val db = FirebaseFirestore.getInstance()

    fun all(): Flow<List<Notice>> = callbackFlow {
        val listener = db.collection("notices").addSnapshotListener { snap, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            if (snap != null) {
                val list = snap.toObjects(Notice::class.java)
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun upsert(notice: Notice) {
        firestore.saveNotice(notice)
    }

    suspend fun markRead(id: Long) {
        // TODO: Implement per-user read status using subcollection or local storage
        // For now, no-op or global update (undesirable). 
        // Leaving empty for this migration step.
    }

    suspend fun markAllRead() {
        // No-op
    }
}
