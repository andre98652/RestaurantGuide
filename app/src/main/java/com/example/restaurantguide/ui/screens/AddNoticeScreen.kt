package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.restaurantguide.viewmodel.NoticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoticeScreen(
    vm: NoticeViewModel,
    ownerId: Long,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("PROMO") }
    var error by remember { mutableStateOf<String?>(null) }
    
    val types = listOf("PROMO" to "Promoción", "NEWS" to "Novedad", "EVENT" to "Evento")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Crear Aviso") })
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = summary,
                onValueChange = { summary = it },
                label = { Text("Resumen") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            // Duration input (default 24h)
            var duration by remember { mutableStateOf("24") }
            OutlinedTextField(
                value = duration,
                onValueChange = { if (it.all { c -> c.isDigit() }) duration = it },
                label = { Text("Duración (Horas) - 0 = Ilimitado") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Text("Tipo de aviso", style = MaterialTheme.typography.titleSmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                types.forEach { (key, label) ->
                    FilterChip(
                        selected = type == key,
                        onClick = { type = key },
                        label = { Text(label) }
                    )
                }
            }
            
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
            
            Spacer(Modifier.weight(1f))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancelar") }
                
                Button(
                    onClick = {
                        if (title.isBlank() || summary.isBlank()) {
                            error = "Completa todos los campos"
                            return@Button
                        }
                        val hours = duration.toIntOrNull() ?: 24
                        vm.addNotice(ownerId, title, summary, type, hours) { success ->
                            if (success) onSaved()
                            else error = "No tienes restaurantes registrados para crear avisos."
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Guardar") }
            }
        }
    }
}
