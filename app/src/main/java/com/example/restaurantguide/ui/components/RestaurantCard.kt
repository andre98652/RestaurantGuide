package com.example.restaurantguide.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.R
import com.example.restaurantguide.ui.theme.StarYellow

@Composable
fun RestaurantCard(
    title: String,
    cuisine: String,
    priceLevel: Int,
    address: String,
    rating: Double,
    onClick: () -> Unit
) {
    ElevatedCard(onClick = onClick) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {

            // Imagen 16:9 (usa un drawable de muestra por ahora)
            Image(
                painter = painterResource(id = R.drawable.sample_restaurant), // crea este drawable
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(10.dp))

            // Título
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(4.dp))

            // Línea debajo del título: chip “cocina”, $$ y dirección
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = cuisine,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(text = "${"$".repeat(priceLevel)} · $address", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(6.dp))

            // Rating
            Text(text = "⭐ $rating", color = StarYellow, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
