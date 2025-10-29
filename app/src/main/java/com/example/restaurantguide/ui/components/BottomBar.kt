package com.example.restaurantguide.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.restaurantguide.ui.Routes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text


@Composable
fun BottomBar(
    current: String,
    onHome: () -> Unit,
    onFavorites: () -> Unit,
    onNotices: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = current == Routes.HOME,
            onClick = onHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = current == Routes.FAVORITES,
            onClick = onFavorites,
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos") }
        )
        NavigationBarItem(
            selected = current == Routes.NOTICES,
            onClick = onNotices,
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Avisos") },
            label = { Text("Avisos") }
        )
        NavigationBarItem(
            selected = current == Routes.PROFILE,
            onClick = onProfile,
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
