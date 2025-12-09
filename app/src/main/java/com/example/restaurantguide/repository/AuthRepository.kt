package com.example.restaurantguide.repository

import com.example.restaurantguide.data.network.FirestoreService
import com.example.restaurantguide.data.prefs.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val prefs: UserPreferences
) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirestoreService()
    
    val userProfile = prefs.profile

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return false
            
            // Sync profile from Firestore
            val profile = firestore.getUserProfile(uid)
            if (profile != null) {
                // profile.id now comes from Firestore "legacyId"
                prefs.setProfile(profile.id, profile.name, profile.email, profile.role)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun register(name: String, email: String, password: String, role: String): Boolean {
        // Let exceptions bubble up to ViewModel
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Error obteniendo UID")
        
        // Generate Legacy ID for internal app usage
        val legacyId = System.currentTimeMillis()

        // Save to Firestore
        firestore.saveUser(uid, name, email, role, legacyId)
        
        // Save local pref
        prefs.setProfile(legacyId, name, email, role)
        return true
    }

    suspend fun logout() {
        auth.signOut()
        prefs.clear()
    }

    suspend fun isLoggedIn(): Boolean {
        // We rely on local prefs for session state to keep UI sync
        // But we can check Auth
        return auth.currentUser != null
    }
}
