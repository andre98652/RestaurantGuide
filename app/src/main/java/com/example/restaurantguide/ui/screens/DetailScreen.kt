package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.data.model.Restaurant
import com.example.restaurantguide.viewmodel.RestaurantViewModel

@Composable
fun DetailScreen(id: Long, vm: RestaurantViewModel, onBack: () -> Unit) {
    var item by remember { mutableStateOf<Restaurant?>(null) }
    LaunchedEffect(id) { vm.getByIdSuspend(id) { item = it } }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalle", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        if (item == null) {
            Text("Cargando…")
        } else {
            Text(item!!.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("${item!!.cuisine}  ${"$".repeat(item!!.priceLevel)} · ${item!!.address}")
            Spacer(Modifier.height(8.dp))
            Text("⭐ ${item!!.rating}")
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { vm.toggleFavorite(id) }) { Text(if (item!!.isFavorite) "Quitar de Favoritos" else "Agregar a Favoritos") }
                OutlinedButton(onClick = { /* Intent a Maps */ }) { Text("Ver ubicación") }
            }
        }
    }
}
