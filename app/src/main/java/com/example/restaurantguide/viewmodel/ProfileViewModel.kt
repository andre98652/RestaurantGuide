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
    // Preferencias locales (donde se guarda el nombre y email en el celular)
    private val prefs = UserPreferences(app)

    // El perfil en tiempo real. Si cambia algo en "prefs", esta variable se actualiza sola.
    val profile: StateFlow<UserProfile> =
        prefs.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile(name = "", email = ""))

    // Removed auto-seed logic as we have explicit login now

    // Guardar cambios en el perfil (Editar nombre/email)
    fun update(name: String, email: String) {
        viewModelScope.launch {
            val current = prefs.profile.first()
            prefs.setProfile(current.id, name.trim(), email.trim(), current.role)
        }
    }

    fun logout() {
        viewModelScope.launch { prefs.clear() }
    }
}
