package com.example.restaurantguide.data.network

import android.net.Uri
import com.example.restaurantguide.data.network.StorageService
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageService {
    private val storage = FirebaseStorage.getInstance("gs://restaurantguideproject-17f08.firebasestorage.app")

    suspend fun uploadImage(uri: Uri, path: String): String {
        val ref = storage.reference.child(path)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun deleteImage(url: String) {
        try {
            val ref = storage.getReferenceFromUrl(url)
            ref.delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
