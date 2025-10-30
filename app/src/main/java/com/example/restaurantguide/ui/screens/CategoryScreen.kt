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
import com.example.restaurantguide.ui.components.RestaurantCard
@Composable
fun CategoryScreen(category: String, vm: RestaurantViewModel, onOpenDetail: (Long) -> Unit) {
    val list = vm.byCuisineFlow(category).collectAsState()
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Categoría: $category", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(list.value, key = { it.id }) { r ->
                RestaurantCard(
                    title = r.name,
                    cuisine = r.cuisine,
                    priceLevel = r.priceLevel,
                    address = r.address,
                    rating = r.rating,
                    imageUrls = r.imageUrls,  // 👈 primera URL como miniatura
                    onClick = { onOpenDetail(r.id) }
                )
            }
        }
    }
}
