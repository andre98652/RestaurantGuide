package com.example.restaurantguide.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.longPreferencesKey

// DataStore instancia
private val Context.dataStore by preferencesDataStore(name = "user_prefs")


data class UserProfile(
    val id: Long = 0,
    val name: String,
    val email: String,
    val role: String = "USER"
)

class UserPreferences(private val context: Context) {
    private object Keys {
        val ID = longPreferencesKey("user_id")
        val NAME = stringPreferencesKey("user_name")
        val EMAIL = stringPreferencesKey("user_email")
        val ROLE = stringPreferencesKey("user_role")
    }

    val profile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            id = prefs[Keys.ID] ?: 0L,
            name = prefs[Keys.NAME] ?: "",
            email = prefs[Keys.EMAIL] ?: "",
            role = prefs[Keys.ROLE] ?: "USER"
        )
    }

    suspend fun setProfile(id: Long, name: String, email: String, role: String) {
        context.dataStore.edit {
            it[Keys.ID] = id
            it[Keys.NAME] = name
            it[Keys.EMAIL] = email
            it[Keys.ROLE] = role
        }
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.remove(Keys.ID)
            it.remove(Keys.NAME)
            it.remove(Keys.EMAIL)
            it.remove(Keys.ROLE)
        }
    }
}
