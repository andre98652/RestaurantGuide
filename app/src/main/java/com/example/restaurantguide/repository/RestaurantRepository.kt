package com.example.restaurantguide.repository

import com.example.restaurantguide.data.model.Restaurant
import com.example.restaurantguide.data.model.Review
import com.example.restaurantguide.data.network.FirestoreService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RestaurantRepository {
    // 1. CONEXIÓN: Instancia que nos permite "hablar" con la Base de Datos en la Nube
    private val firestore = FirestoreService()
    private val storage = com.example.restaurantguide.data.network.StorageService()
    
    // Conexión directa para escuchar cambios en tiempo real (Flows)
    private val db = FirebaseFirestore.getInstance()

    suspend fun uploadImage(uri: android.net.Uri, path: String): String {
        return storage.uploadImage(uri, path)
    }

    suspend fun deleteImage(url: String) {
        storage.deleteImage(url)
    }

    // 2. ESCUCHA EN TIEMPO REAL:
    // Esta función devuelve un "Flow" (un flujo de datos vivo).
    // A diferencia de una consulta normal que pide datos una vez, esto se queda "escuchando".
    val restaurants: Flow<List<Restaurant>> = callbackFlow {
        // "addSnapshotListener" es el "micrófono" conectado a la colección "restaurants" en la nube.
        // Se activa CADA VEZ que alguien (tú u otro usuario) cambia algo en la base de datos.
        val listener = db.collection("restaurants").addSnapshotListener { snap, e ->
            if (e != null) {
                close(e) // Si hay error de conexión, cerramos el flujo.
                return@addSnapshotListener
            }
            if (snap != null) {
                // Si llegan datos nuevos, los convertimos a objetos Restaurant y los enviamos a la App.
                val list = snap.toObjects(Restaurant::class.java)
                trySend(list)
            }
        }
        // Cuando la pantalla se cierra, "colgamos" la llamada para no gastar datos.
        awaitClose { listener.remove() }
    }

    suspend fun getById(id: Long): Restaurant? {
        return firestore.getRestaurant(id)
    }

    suspend fun getByOwner(ownerId: Long): List<Restaurant> {
        return firestore.getRestaurantsByOwner(ownerId)
    }

    suspend fun upsert(r: Restaurant) {
        firestore.saveRestaurant(r)
    }
    
    // Favorites
    suspend fun toggleFavorite(userId: Long, restaurantId: Long) {
        firestore.toggleFavorite(userId, restaurantId)
    }
    
    suspend fun getFavoriteCount(restaurantId: Long): Int {
        return firestore.getFavoriteCount(restaurantId)
    }

    // Since we need "isFavorite" for the UI, we ideally need a flow of favorites for the user
    fun getFavoritesForUser(userId: Long): Flow<List<Long>> = callbackFlow {
        val listener = db.collection("favorites")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snap, e ->
                if (e != null) return@addSnapshotListener
                if (snap != null) {
                    val list = snap.documents.mapNotNull { it.getLong("restaurantId") }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    // Reviews
    suspend fun getReviewsForRestaurant(restaurantId: Long): Flow<List<Review>> = callbackFlow {
        val listener = db.collection("reviews")
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snap, e ->
                if (e != null) return@addSnapshotListener
                if (snap != null) {
                    val list = snap.toObjects(Review::class.java)
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }
    
    suspend fun addReview(review: Review) {
        firestore.saveReview(review)
    }
}
