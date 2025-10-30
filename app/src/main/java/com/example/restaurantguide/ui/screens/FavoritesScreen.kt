package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.viewmodel.RestaurantViewModel

@Composable
fun FavoritesScreen(vm: RestaurantViewModel, onOpenDetail: (Long) -> Unit) {
    val favs = vm.favorites.collectAsState()
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Favoritos", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        if (favs.value.isEmpty()) {
            Text("Aún no tienes favoritos ❤️")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favs.value) { r ->
                    RestaurantCardSimple(
                        title = r.name,
                        subtitle = "${r.cuisine}  ${"$".repeat(r.priceLevel)} · ${r.address}",
                        rating = r.rating,
                        onClick = { onOpenDetail(r.id) }
                    )
                }
            }
        }
    }
}
