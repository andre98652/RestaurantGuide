package com.example.restaurantguide.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore instancia
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

data class UserProfile(val name: String, val email: String)

class UserPreferences(private val context: Context) {
    private object Keys {
        val NAME: Preferences.Key<String> = stringPreferencesKey("user_name")
        val EMAIL: Preferences.Key<String> = stringPreferencesKey("user_email")
    }

    val profile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[Keys.NAME] ?: "",
            email = prefs[Keys.EMAIL] ?: ""
        )
    }

    suspend fun setName(name: String) {
        context.dataStore.edit { it[Keys.NAME] = name }
    }

    suspend fun setEmail(email: String) {
        context.dataStore.edit { it[Keys.EMAIL] = email }
    }

    suspend fun setProfile(name: String, email: String) {
        context.dataStore.edit {
            it[Keys.NAME] = name
            it[Keys.EMAIL] = email
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.remove(Keys.NAME)
            it.remove(Keys.EMAIL)
        }
    }
}
