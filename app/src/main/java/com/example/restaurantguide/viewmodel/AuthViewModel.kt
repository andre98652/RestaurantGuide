package com.example.restaurantguide.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantguide.data.database.AppDatabase
import com.example.restaurantguide.data.prefs.UserPreferences
import com.example.restaurantguide.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER }

// Estado de la pantalla de Autenticación (Login/Registro)
data class AuthUiState(
    val id: Long = 0, // Added id
    val mode: AuthMode = AuthMode.LOGIN,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val role: String = "USER", // "USER" or "RESTAURANT"
    val showSuccess: Boolean = false
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    // Repositorio de Autenticación
    private val repo = AuthRepository(
        UserPreferences(app)
    )

    // El estado mutable privado (solo el ViewModel lo cambia)
    private val _uiState = MutableStateFlow(AuthUiState())
    // El estado público (la Vista solo lo lee)
    val uiState = _uiState.asStateFlow()

    init {
        // Escuchamos cambios en el perfil (UserPreferences/Repositorio)
        // Si el repositorio dice que hay usuario, actualizamos el estado a "Logueado".
        viewModelScope.launch {
            repo.userProfile.collect { profile ->
                _uiState.update {
                    it.copy(
                        isLoggedIn = profile.id != 0L,
                        id = profile.id,
                        role = if (profile.id != 0L) profile.role else it.role,
                        // Don't overwrite name/email inputs if not logged in, users might be typing?
                        // Actually if we interpret userProfile as single source of truth for "Session":
                        name = if (profile.id != 0L) profile.name else it.name,
                        email = if (profile.id != 0L) profile.email else it.email
                    )
                }
            }
        }
    }

    // Manejadores de eventos (cuando el usuario escribe o cambia el modo)
    fun onToggleMode() {
        _uiState.update { current ->
            current.copy(
                mode = if (current.mode == AuthMode.LOGIN) AuthMode.REGISTER else AuthMode.LOGIN,
                error = null,
                showSuccess = false
            )
        }
    }

    fun onEmailChange(v: String) { _uiState.update { it.copy(email = v) } }
    fun onPasswordChange(v: String) { _uiState.update { it.copy(password = v) } }
    fun onNameChange(v: String) { _uiState.update { it.copy(name = v) } }
    fun onRoleChange(v: String) { _uiState.update { it.copy(role = v) } }

    // Al hacer clic en el botón "Ingresar" o "Registrar"
    fun submit() {
        val s = _uiState.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _uiState.update { it.copy(error = "Campos vacíos") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                if (s.mode == AuthMode.LOGIN) {
                    val success = repo.login(s.email, s.password)
                    if (success) {
                        _uiState.update { it.copy(isLoggedIn = true, isLoading = false) }
                    } else {
                        _uiState.update { it.copy(error = "Credenciales inválidas", isLoading = false) }
                    }
                } else {
                    if (s.name.isBlank()) {
                        _uiState.update { it.copy(error = "Nombre requerido", isLoading = false) }
                        return@launch
                    }
                    val success = repo.register(s.name, s.email, s.password, s.role)
                    // If register throws, it goes to catch block. If it returns true:
                    if (success) {
                        _uiState.update { it.copy(isLoggedIn = true, isLoading = false, showSuccess = true) }
                    }
                }
            } catch (e: Exception) {
                // Show the real error (e.g. Permission Denied, Network Error, etc.)
                _uiState.update { it.copy(error = e.localizedMessage ?: "Error desconocido", isLoading = false) }
            }
        }
    }
    
    // Cerrar sesión
    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _uiState.update { AuthUiState(mode = AuthMode.LOGIN) }
        }
    }
}
