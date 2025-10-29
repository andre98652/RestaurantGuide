package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Perfil", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        Surface(tonalElevation = 2.dp, shape = MaterialTheme.shapes.extraLarge) {
            Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                Text("👤", style = MaterialTheme.typography.headlineLarge)
            }
        }
        Spacer(Modifier.height(24.dp))
        ElevatedCard(Modifier.fillMaxWidth()) { Text("Usuario: Juan Pérez", Modifier.padding(16.dp)) }
        Spacer(Modifier.height(8.dp))
        ElevatedCard(Modifier.fillMaxWidth()) { Text("Email: juanperez@gmail.com", Modifier.padding(16.dp)) }
        Spacer(Modifier.height(24.dp))
        Button(onClick = { /* limpiar sesión simulada */ }) { Text("Cerrar Sesión") }
    }
}
