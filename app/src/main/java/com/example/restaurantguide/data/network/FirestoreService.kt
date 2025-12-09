package com.example.restaurantguide.data.network

import com.example.restaurantguide.data.model.User
import com.example.restaurantguide.data.prefs.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    // --- Users ---
    suspend fun saveUser(uid: String, name: String, email: String, role: String, legacyId: Long) {
        val user = hashMapOf(
            "id" to uid,
            "legacyId" to legacyId, // Saved for backward compatibility
            "name" to name,
            "email" to email,
            "role" to role
        )
        db.collection("users").document(uid).set(user, SetOptions.merge()).await()
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = db.collection("users").document(uid).get().await()
        return if (doc.exists()) {
            UserProfile(
                id = doc.getLong("legacyId") ?: 0L, // Retrieve legacy ID
                name = doc.getString("name") ?: "",
                email = doc.getString("email") ?: "",
                role = doc.getString("role") ?: "USER"
            )
        } else null
    }

    // --- Restaurants ---
    suspend fun getRestaurants(): List<com.example.restaurantguide.data.model.Restaurant> {
        return db.collection("restaurants").get().await().map { doc ->
            doc.toObject(com.example.restaurantguide.data.model.Restaurant::class.java)
        }
    }

    suspend fun getRestaurantsByOwner(ownerId: Long): List<com.example.restaurantguide.data.model.Restaurant> {
        return db.collection("restaurants")
            .whereEqualTo("ownerId", ownerId)
            .get().await()
            .map { it.toObject(com.example.restaurantguide.data.model.Restaurant::class.java) }
    }

    suspend fun saveRestaurant(r: com.example.restaurantguide.data.model.Restaurant) {
        // Use ID as doc ID if > 0, else generate
        val id = if (r.id == 0L) System.currentTimeMillis() else r.id
        val toSave = r.copy(id = id)
        db.collection("restaurants").document(id.toString()).set(toSave).await()
    }
    
    suspend fun getRestaurant(id: Long): com.example.restaurantguide.data.model.Restaurant? {
         val snap = db.collection("restaurants").document(id.toString()).get().await()
         return if (snap.exists()) snap.toObject(com.example.restaurantguide.data.model.Restaurant::class.java) else null
    }

    // --- Notices ---
    suspend fun getNotices(): List<com.example.restaurantguide.data.model.Notice> {
        return db.collection("notices").get().await().map { doc ->
            doc.toObject(com.example.restaurantguide.data.model.Notice::class.java)
        }
    }

    suspend fun saveNotice(n: com.example.restaurantguide.data.model.Notice) {
        val id = if (n.id == 0L) System.currentTimeMillis() else n.id
        val toSave = n.copy(id = id)
        db.collection("notices").document(id.toString()).set(toSave).await()
    }

    // --- Favorites ---
    suspend fun toggleFavorite(userId: Long, restaurantId: Long) {
        // Collection: users/{uid}/favorites/{restId}
        // Ideally we map userId(Long) to FirebaseUid(String). 
        // CAUTION: This app mixes Long IDs (Legacy) and Auth Strings.
        // For simplicity: We will use a top-level collection "favorites" 
        // with composite ID "userId_restId"
        
        val docId = "${userId}_$restaurantId"
        val docRef = db.collection("favorites").document(docId)
        val snap = docRef.get().await()
        if (snap.exists()) {
            docRef.delete().await()
        } else {
            val data = hashMapOf("userId" to userId, "restaurantId" to restaurantId)
            docRef.set(data).await()
        }
    }
    
    suspend fun getFavorites(userId: Long): List<Long> {
        return db.collection("favorites")
            .whereEqualTo("userId", userId)
            .get().await()
            .mapNotNull { it.getLong("restaurantId") }
    }

    suspend fun getFavoriteCount(restaurantId: Long): Int {
        return db.collection("favorites")
            .whereEqualTo("restaurantId", restaurantId)
            .get().await()
            .size()
    }

    // --- Reviews ---
    suspend fun getReviews(restaurantId: Long): List<com.example.restaurantguide.data.model.Review> {
         return db.collection("reviews")
            .whereEqualTo("restaurantId", restaurantId)
            .get().await()
            .map { it.toObject(com.example.restaurantguide.data.model.Review::class.java) }
    }

    suspend fun saveReview(review: com.example.restaurantguide.data.model.Review) {
        val id = if (review.id == 0L) System.currentTimeMillis() else review.id
        val toSave = review.copy(id = id)
        db.collection("reviews").document(id.toString()).set(toSave).await()
    }
}
