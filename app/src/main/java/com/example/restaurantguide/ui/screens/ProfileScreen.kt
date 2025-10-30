package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.restaurantguide.viewmodel.ProfileViewModel
import com.example.restaurantguide.ui.theme.RedPrimary

@Composable
fun ProfileScreen(vm: ProfileViewModel = viewModel()) {
    val profile by vm.profile.collectAsState()

    var showEdit by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        // Campos read-only
        OutlinedTextField(
            value = profile.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = profile.email,
            onValueChange = {},
            readOnly = true,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Botones
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { showEdit = true },
                modifier = Modifier.weight(1f)
            ) { Text("Editar") }

            Button(
                onClick = { vm.logout() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
            ) { Text("Cerrar sesión") }
        }
    }

    if (showEdit) {
        EditProfileDialog(
            initialName = profile.name,
            initialEmail = profile.email,
            onDismiss = { showEdit = false },
            onSave = { name, email ->
                vm.update(name, email)
                showEdit = false
            }
        )
    }
}

@Composable
private fun EditProfileDialog(
    initialName: String,
    initialEmail: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar perfil", fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, email) },
                enabled = name.isNotBlank() && email.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
