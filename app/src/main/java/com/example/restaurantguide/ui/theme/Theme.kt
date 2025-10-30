package com.example.restaurantguide.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = RedPrimary,
    onPrimary = OnPrimary,
    secondary = RedDark,
    surface = SurfaceBg,
    background = SurfaceBg
)

@Composable
fun RestaurantGuideTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTheme.typography, // puedes personalizar luego
        shapes = MaterialTheme.shapes,
        content = content
    )
}
