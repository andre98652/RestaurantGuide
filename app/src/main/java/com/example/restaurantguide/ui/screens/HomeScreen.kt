package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.viewmodel.RestaurantViewModel

@Composable
fun HomeScreen(
    vm: RestaurantViewModel,
    onOpenDetail: (Long) -> Unit,
    onOpenCategory: (String) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenNotices: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val ui by vm.home.collectAsState()

    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Restaurantes", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = ui.query,
            onValueChange = vm::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Busca por nombre o tipo de cocina...") }
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos","Peruana","Italiana","Japonesa","Parrillas").forEach { c ->
                FilterChip(
                    selected = ui.selectedCuisine == c,
                    onClick = { vm.selectCuisine(c) },
                    label = { Text(c) }
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ui.items) { r ->
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
