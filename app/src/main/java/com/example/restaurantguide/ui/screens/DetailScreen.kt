package com.example.restaurantguide.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.restaurantguide.data.model.Restaurant
import com.example.restaurantguide.viewmodel.RestaurantViewModel
import com.example.restaurantguide.ui.theme.OnPrimary
import com.example.restaurantguide.ui.theme.RedPrimary
import com.example.restaurantguide.ui.theme.StarYellow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(id: Long, vm: RestaurantViewModel, onBack: () -> Unit) {
    var item by remember { mutableStateOf<Restaurant?>(null) }
    val userId by vm.userId.collectAsStateWithLifecycle()
    val favorites by vm.favorites.collectAsStateWithLifecycle()
    val reviews by vm.getReviews(id).collectAsStateWithLifecycle(initialValue = emptyList())
    
    val scroll = rememberScrollState()
    val context = LocalContext.current
    
    // FULL SCREEN IMAGE STATE
    var showFullScreenImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) { vm.getByIdSuspend(id) { item = it } }
    
    val isFav = favorites.any { it.id == id }

    if (showFullScreenImage != null) {
        Dialog(
            onDismissRequest = { showFullScreenImage = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(Modifier.fillMaxSize().background(Color.Black)) {
                AsyncImage(
                    model = showFullScreenImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { showFullScreenImage = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.name ?: "Detalle") }
            )
        }
    ) { padding ->
        if (item == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val restaurant = item!!

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
                        .clickable { showFullScreenImage = mainUrl } // CLICKABLE
                )

                Spacer(Modifier.height(12.dp))

                // Carrusel
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

                Column(Modifier.padding(horizontal = 16.dp)) {
                    Text(restaurant.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(4.dp))
                    Text("${restaurant.cuisine} • ${"$".repeat(restaurant.priceLevel)}")
                    Text(restaurant.address, style = MaterialTheme.typography.bodySmall)
                    
                    // LOCATION LINK
                    if (restaurant.latitude != 0.0 && restaurant.longitude != 0.0) {
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val lat = restaurant.latitude
                                val lng = restaurant.longitude
                                val label = Uri.encode(restaurant.name)
                                val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    val fallback = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    context.startActivity(fallback)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Ver ubicación en Mapa")
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("⭐ ${restaurant.rating}", color = StarYellow, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    Text(restaurant.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(20.dp))

                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { vm.toggleFavorite(id) },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary, contentColor = OnPrimary),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(if (isFav) "Quitar de favoritos" else "Agregar a favoritos")
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                    
                    // Reviews Section
                    Text("Reseñas", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    
                    if (reviews.isEmpty()) {
                        Text("No hay reseñas aún.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        reviews.forEach { review ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(Modifier.padding(8.dp)) {
                                    Text(review.userName, fontWeight = FontWeight.Bold)
                                    Text("⭐".repeat(review.rating))
                                    Text(review.comment)
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Add Review
                    if (userId != 0L) {
                        Text("Escribe una reseña", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        
                        var comment by remember { mutableStateOf("") }
                        var rating by remember { mutableIntStateOf(5) }
                        
                        // Star Rating UI
                        Row(verticalAlignment = Alignment.CenterVertically) {
                           Text("Calificación: ")
                           (1..5).forEach { star ->
                               IconButton(onClick = { rating = star }) {
                                   Icon(
                                       imageVector = if (star <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                       contentDescription = "Star $star",
                                       tint = StarYellow
                                   )
                               }
                           }
                        }
                        
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text("Comentario") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (comment.isNotBlank()) {
                                    vm.addReview(id, comment, rating)
                                    comment = ""
                                    rating = 5 // reset
                                }
                            },
                            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
                        ) {
                            Text("Enviar")
                        }
                    }
                }
            }
        }
    }
}
