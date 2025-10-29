package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RestaurantCardSimple(
    title: String,
    subtitle: String,
    rating: Double,
    onClick: () -> Unit
) {
    ElevatedCard(onClick = onClick) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            // Placeholder de imagen (luego lo cambiamos por una Image real o Async)
            Surface(Modifier.fillMaxWidth().height(160.dp), tonalElevation = 2.dp) {}
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            Text("⭐ $rating", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
