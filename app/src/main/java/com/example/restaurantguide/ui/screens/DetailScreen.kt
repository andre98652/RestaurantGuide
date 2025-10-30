package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.restaurantguide.data.model.Restaurant
import com.example.restaurantguide.viewmodel.RestaurantViewModel
import com.example.restaurantguide.ui.theme.OnPrimary
import com.example.restaurantguide.ui.theme.RedPrimary
import com.example.restaurantguide.ui.theme.StarYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(id: Long, vm: RestaurantViewModel, onBack: () -> Unit) {
    var item by remember { mutableStateOf<Restaurant?>(null) }
    val scroll = rememberScrollState()

    LaunchedEffect(id) { vm.getByIdSuspend(id) { item = it } }

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

            // 1) Lista de imágenes (usa imageUrls si hay; si no, fallback)
            val images = remember(restaurant.imageUrls) {
                val fromDb = restaurant.imageUrls
                    .takeIf { it.isNotBlank() }
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?: emptyList()

                if (fromDb.isNotEmpty()) fromDb
                else listOf(
                    "https://picsum.photos/1024/600?random=${restaurant.id}",
                    "https://picsum.photos/1024/600?random=${restaurant.id}1",
                    "https://picsum.photos/1024/600?random=${restaurant.id}2",
                    "https://picsum.photos/1024/600?random=${restaurant.id}3",
                )
            }

            // 2) Imagen seleccionada (se guarda con rememberSaveable)
            var selected by rememberSaveable(restaurant.id) { mutableStateOf(0) }
            val mainUrl = images.getOrNull(selected) ?: images.first()

            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(scroll)
                    .fillMaxSize()
            ) {
                // Imagen principal
                AsyncImage(
                    model = mainUrl,
                    contentDescription = restaurant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(Modifier.height(12.dp))

                // Carrusel de miniaturas (LazyRow)
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    itemsIndexed(images, key = { idx, url -> "$idx-$url" }) { idx, url ->
                        val isSelected = idx == selected

                        Surface(
                            tonalElevation = if (isSelected) 3.dp else 1.dp,
                            shape = RoundedCornerShape(10.dp),
                            border = if (isSelected) BorderStroke(2.dp, RedPrimary) else null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selected = idx }
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }

                        if (idx != images.lastIndex) Spacer(Modifier.width(8.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Información
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        restaurant.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("${restaurant.cuisine} • ${"$".repeat(restaurant.priceLevel)}")
                    Text(restaurant.address, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Text("⭐ ${restaurant.rating}", color = StarYellow, style = MaterialTheme.typography.bodyLarge)

                    Spacer(Modifier.height(12.dp))
                    Text(restaurant.description, style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(20.dp))

                    // Acciones
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var isFav by remember { mutableStateOf(restaurant.isFavorite) }

                        Button(
                            onClick = {
                                vm.toggleFavorite(id)
                                isFav = !isFav
                                item = item?.copy(isFavorite = isFav)
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
                            onClick = { /* se implementará en Entregable 3 */ },
                            shape = MaterialTheme.shapes.large
                        ) { Text("Ver ubicación") }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}
