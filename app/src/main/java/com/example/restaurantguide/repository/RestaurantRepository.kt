package com.example.restaurantguide.repository

import com.example.restaurantguide.data.model.Restaurant
import com.example.restaurantguide.data.model.Review
import com.example.restaurantguide.data.network.FirestoreService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RestaurantRepository(private val db: com.example.restaurantguide.data.database.AppDatabase) {
    // 1. FUENTES DE DATOS
    private val firestore = FirestoreService()
    private val cloudDb = FirebaseFirestore.getInstance() // Firebase Realtime
    private val localDb = db.restaurantDao() // Room Realtime
    private val favDao = db.favoriteDao()
    private val reviewDao = db.reviewDao()
    private val storage = com.example.restaurantguide.data.network.StorageService()

    // 2. FUENTE DE VERDAD ÚNICA (Room)
    // La App SIEMPRE escucha a la base de datos local.
    // Esto garantiza que funcione OFFLINE y cargue INSTANTÁNEAMENTE.
    val restaurants: Flow<List<Restaurant>> = localDb.getAll()

    // 3. SINCRONIZACIÓN (Segundo Plano)
    // Conectamos "el cable" de Firebase. Cuando cambia la nube -> actualizamos Room.
    init {
        // Sync Restaurants
        cloudDb.collection("restaurants").addSnapshotListener { snap, _ ->
            if (snap != null) {
                val list = snap.toObjects(Restaurant::class.java)
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    // Borramos y reescribimos para mantener consistencia total (simple sync)
                     // En una app real compleja usaríamos diffing, pero para este demo:
                     // 1. Upsert all from cloud
                     localDb.upsertAll(list) 
                }
            }
        }
    }

    // --- ACCIONES (Escriben primero en la Nube, luego la nube avisa y bajamos el dato) ---

    // Sube una imagen a Firebase Storage
    suspend fun uploadImage(uri: android.net.Uri, path: String): String {
        return storage.uploadImage(uri, path)
    }

    suspend fun deleteImage(url: String) {
        storage.deleteImage(url)
    }

    suspend fun getById(id: Long): Restaurant? {
        // Primero intentamos local (rápido)
        return localDb.getById(id) ?: firestore.getRestaurant(id)
    }

    suspend fun getByOwner(ownerId: Long): List<Restaurant> {
       return localDb.getByOwner(ownerId).ifEmpty { 
           // Fallback a nube si no hay nada local (ej: primera vez)
           firestore.getRestaurantsByOwner(ownerId) 
       }
    }

    suspend fun upsert(r: Restaurant) {
        // Guardamos en Nube. El listener de arriba detectará el cambio y actualizará Room.
        firestore.saveRestaurant(r)
    }
    
    // --- FAVORITOS (Offline First) ---
    
    // Escucha favoritos LOCALES
    fun getFavoritesForUser(userId: Long): Flow<List<Long>> {
        // Sincronizar Favoritos de este usuario
        cloudDb.collection("favorites")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    val list = snap.toObjects(com.example.restaurantguide.data.model.Favorite::class.java)
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        favDao.upsertAll(list)
                    }
                }
            }
            
        // Retornar ID's desde local
        return favDao.getFavoritesForUser(userId).map { list -> list.map { it.restaurantId } }
    }

    suspend fun toggleFavorite(userId: Long, restaurantId: Long) {
        // Optimistic Update: Escribir en local inmediatamente (para que el UI responda rápido)
        // Luego mandar a nube.
        val isFav = favDao.isFavorite(userId, restaurantId) > 0
        if (isFav) {
            favDao.removeFavorite(userId, restaurantId)
        } else {
            favDao.addFavorite(com.example.restaurantguide.data.model.Favorite(userId = userId, restaurantId = restaurantId))
        }
        
        // Sync Cloud
        firestore.toggleFavorite(userId, restaurantId)
    }
    
    suspend fun getFavoriteCount(restaurantId: Long): Int {
        return favDao.getFavoriteCount(restaurantId)
    }

    // --- REVIEWS (Offline First) ---
    
    suspend fun getReviewsForRestaurant(restaurantId: Long): Flow<List<Review>> {
         // Sync Reviews for this restaurant
         cloudDb.collection("reviews")
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    val list = snap.toObjects(Review::class.java)
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                        list.forEach { reviewDao.insertReview(it) } // Insert is REPLACE
                    }
                }
            }
         return reviewDao.getReviewsForRestaurant(restaurantId)
    }
    
    suspend fun addReview(review: Review) {
        // Save cloud (listener updates local)
        firestore.saveReview(review)
    }
}
