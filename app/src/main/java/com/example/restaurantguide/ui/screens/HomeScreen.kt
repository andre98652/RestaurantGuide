package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.ui.components.CategoryChips
import com.example.restaurantguide.ui.components.RestaurantCard
import com.example.restaurantguide.viewmodel.RestaurantViewModel
import androidx.compose.runtime.collectAsState

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon

@Composable
fun HomeScreen(
    vm: RestaurantViewModel,
    userRole: String, // "USER" or "RESTAURANT"
    onOpenDetail: (Long) -> Unit,
    onOpenCategory: (String) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenNotices: () -> Unit,
    onOpenProfile: () -> Unit,
    onAddRestaurant: () -> Unit
) {
    val ui by vm.home.collectAsState()
    val listState = rememberLazyListState()

    val categories = remember {
        listOf("Todos", "Peruana", "Italiana", "Japonesa", "Parrillas")
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Search + Botón Filtros
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ui.query,
                    onValueChange = vm::updateQuery,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Busca por nombre o tipo de cocina...") },
                    singleLine = true
                )
                Button(onClick = { /* abrir bottom sheet de filtros */ }) {
                    Text("Filtros")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Chips
            CategoryChips(
                categories = categories,
                selected = ui.selectedCuisine,
                onSelect = { choice ->
                    vm.selectCuisine(choice)
                    if (choice != "Todos") onOpenCategory(choice)
                }
            )

            Spacer(Modifier.height(12.dp))

            // Lista
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(ui.items, key = { it.id }) { r ->
                    RestaurantCard(
                        title = r.name,
                        cuisine = r.cuisine,
                        priceLevel = r.priceLevel,
                        address = r.address,
                        rating = r.rating,
                        onClick = { onOpenDetail(r.id) },
                        imageUrls = r.imageUrls
                    )
                }
            }
        }

        if (userRole == "RESTAURANT") {
            val myRestaurant by vm.myRestaurant.collectAsState(null)
            FloatingActionButton(
                onClick = onAddRestaurant,
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                if (myRestaurant != null) {
                    Icon(androidx.compose.material.icons.Icons.Default.Edit, contentDescription = "Editar Mi Restaurante")
                } else {
                    Icon(androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Agregar Restaurante")
                }
            }
        }
    }
}