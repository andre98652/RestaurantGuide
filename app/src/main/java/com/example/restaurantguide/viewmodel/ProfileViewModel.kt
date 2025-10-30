package com.example.restaurantguide.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantguide.data.prefs.UserPreferences
import com.example.restaurantguide.data.prefs.UserProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = UserPreferences(app)

    val profile: StateFlow<UserProfile> =
        prefs.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile("", ""))

    init {
        // Seed en primer arranque si está vacío
        viewModelScope.launch {
            val current = prefs.profile.first()
            if (current.name.isBlank() && current.email.isBlank()) {
                prefs.setProfile(name = "Juan Pérez", email = "juanperez@gmail.com")
            }
        }
    }

    fun update(name: String, email: String) {
        viewModelScope.launch { prefs.setProfile(name.trim(), email.trim()) }
    }

    fun logout() {
        viewModelScope.launch { prefs.clear() }
    }
}
