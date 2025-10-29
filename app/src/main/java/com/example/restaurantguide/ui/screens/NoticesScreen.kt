package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.data.model.Notice
import com.example.restaurantguide.viewmodel.NoticeViewModel

@Composable
fun NoticesScreen(nm: NoticeViewModel, onOpenRestaurant: (Long?) -> Unit) {
    val list = nm.notices.collectAsState()
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Text("Avisos", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        if (list.value.isEmpty()) {
            Text("No hay avisos por ahora 🎉")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(list.value) { n -> NoticeCardSimple(n) { onOpenRestaurant(n.restaurantId) } }
            }
        }
    }
}

@Composable
private fun NoticeCardSimple(n: Notice, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick) {
        Text(
            text = when (n.type) {
                "PROMO" -> "🎉 Promoción: ${n.title} — ${n.summary}"
                "NEWS"  -> "🧑‍🍳 Novedad: ${n.title} — ${n.summary}"
                else    -> "🍣 Evento: ${n.title} — ${n.summary}"
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}
