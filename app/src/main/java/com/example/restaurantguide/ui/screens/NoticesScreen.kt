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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.ui.theme.RedPrimary
import com.example.restaurantguide.ui.theme.OnPrimary
import com.example.restaurantguide.ui.theme.StarYellow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width


@Composable
fun NoticesScreen(nm: NoticeViewModel, onOpenRestaurant: (Long?) -> Unit) {
    val ui by nm.ui.collectAsState()
    val types = listOf("Todos","PROMO","NEWS","EVENT")

    Column(Modifier.fillMaxSize().padding(12.dp)) {

        // Barra superior: búsqueda + botón "Marcar todo"
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = ui.query,
                onValueChange = nm::setQuery,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Buscar aviso o restaurante...") },
                singleLine = true
            )
            TextButton(onClick = { nm.markAllRead() }) {
                Text("Marcar todo")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Chips de tipo
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(types) { t ->
                val selected = ui.type == t
                FilterChip(
                    selected = selected,
                    onClick = { nm.setType(t) },
                    label = {
                        Text(
                            when (t) {
                                "PROMO" -> "Promoción"
                                "NEWS" -> "Novedad"
                                "EVENT" -> "Evento"
                                else -> "Todos"
                            }
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RedPrimary,
                        selectedLabelColor = OnPrimary
                    )
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (ui.items.isEmpty()) {
            Text("No hay avisos por ahora 🎉", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ui.items, key = { it.id }) { n ->
                    NoticeCard(n,
                        onClick = {
                            nm.markRead(n.id)
                            onOpenRestaurant(n.restaurantId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NoticeCard(n: Notice, onClick: () -> Unit) {
    val emoji = when (n.type) {
        "PROMO" -> "🎉"
        "NEWS"  -> "🧑‍🍳"
        else    -> "🍣"
    }
    val unreadDot = if (!n.isRead) " •" else ""

    ElevatedCard(onClick = onClick) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
                "$emoji ${labelFor(n.type)}$unreadDot",
                style = MaterialTheme.typography.labelLarge,
                color = if (!n.isRead) RedPrimary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(n.title, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(n.summary, style = MaterialTheme.typography.bodyMedium)
            if (n.priority >= 2) {
                Spacer(Modifier.height(6.dp))
                AssistChip(onClick = {}, label = { Text("Prioridad") }, leadingIcon = {
                    Text("🔥")
                })
            }
        }
    }
}

private fun labelFor(type: String) = when (type) {
    "PROMO" -> "Promoción"
    "NEWS" -> "Novedad"
    "EVENT" -> "Evento"
    else -> "Aviso"
}
