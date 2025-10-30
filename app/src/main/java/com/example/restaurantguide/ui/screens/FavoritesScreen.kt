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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.ui.components.RestaurantCard
@Composable
fun FavoritesScreen(
    vm: RestaurantViewModel,
    onOpenDetail: (Long) -> Unit
) {
    // 👇 Importante: usar la PROPIEDAD estable del VM (no una función que cree el flow cada vez)
    val favs by vm.favorites.collectAsState()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Favoritos", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        if (favs.isEmpty()) {
            Text("Aún no tienes restaurantes en favoritos 👀")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favs, key = { it.id }) { r ->
                    RestaurantCard(
                        title = r.name,
                        cuisine = r.cuisine,
                        priceLevel = r.priceLevel,
                        address = r.address,
                        rating = r.rating,
                        imageUrls = r.imageUrls,   // 👈 usa la primera URL como miniatura
                        onClick = { onOpenDetail(r.id) }
                    )
                }
            }
        }
    }
}