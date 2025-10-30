package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.restaurantguide.data.model.Restaurant
import com.example.restaurantguide.viewmodel.RestaurantViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.restaurantguide.ui.theme.RedPrimary
import com.example.restaurantguide.ui.theme.OnPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(id: Long, vm: RestaurantViewModel, onBack: () -> Unit) {
    var item by remember { mutableStateOf<Restaurant?>(null) }
    val scroll = rememberScrollState()

    LaunchedEffect(id) {
        vm.getByIdSuspend(id) { item = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (item == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val restaurant = item!!

            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(scroll)
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = "https://picsum.photos/800/400?random=${restaurant.id}",
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                Column(Modifier.padding(16.dp)) {
                    Text(
                        restaurant.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${restaurant.cuisine} • ${"$".repeat(restaurant.priceLevel)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        restaurant.address,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("⭐ ${restaurant.rating}", style = MaterialTheme.typography.bodyLarge)

                    Spacer(Modifier.height(16.dp))
                    Text(
                        restaurant.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var isFav by remember { mutableStateOf(restaurant.isFavorite) }

                        Button(
                            onClick = {
                                vm.toggleFavorite(id)
                                isFav = !isFav
                                item = item?.copy(isFavorite = isFav) // refresca la UI local
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedPrimary,
                                contentColor = OnPrimary
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (isFav) "Quitar de favoritos" else "Agregar a favoritos")
                        }


                        OutlinedButton(
                            onClick = { /* abrir mapa en Entregable 3 */ },
                            shape = MaterialTheme.shapes.large
                        ) { Text("Ver ubicación") }

                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}
