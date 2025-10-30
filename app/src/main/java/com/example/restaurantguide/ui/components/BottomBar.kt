package com.example.restaurantguide.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.restaurantguide.ui.Routes
import com.example.restaurantguide.ui.theme.OnPrimary
import com.example.restaurantguide.ui.theme.RedPrimary

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
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OnPrimary,
                selectedTextColor = OnPrimary,
                indicatorColor = RedPrimary
            )
        )
        NavigationBarItem(
            selected = current == Routes.FAVORITES,
            onClick = onFavorites,
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OnPrimary,
                selectedTextColor = OnPrimary,
                indicatorColor = RedPrimary
            )
        )
        NavigationBarItem(
            selected = current == Routes.NOTICES,
            onClick = onNotices,
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Avisos") },
            label = { Text("Avisos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OnPrimary,
                selectedTextColor = OnPrimary,
                indicatorColor = RedPrimary
            )
        )
        NavigationBarItem(
            selected = current == Routes.PROFILE,
            onClick = onProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = OnPrimary,
                selectedTextColor = OnPrimary,
                indicatorColor = RedPrimary
            )
        )
    }
}
