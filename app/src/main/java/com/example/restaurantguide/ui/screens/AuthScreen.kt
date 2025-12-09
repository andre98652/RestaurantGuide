package com.example.restaurantguide.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.restaurantguide.viewmodel.AuthMode
import com.example.restaurantguide.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    vm: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (state.mode == AuthMode.LOGIN) "Bienvenido" else "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (state.mode == AuthMode.REGISTER) {
            OutlinedTextField(
                value = state.name,
                onValueChange = vm::onNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Role Selection
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("USER", "RESTAURANT").forEach { role ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (state.role == role),
                                onClick = { vm.onRoleChange(role) }
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (state.role == role),
                            onClick = { vm.onRoleChange(role) }
                        )
                        Text(
                            text = if (role == "USER") "Usuario" else "Restaurante",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = state.email,
            onValueChange = vm::onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = vm::onPasswordChange,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = state.error!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = vm::submit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            else Text(if (state.mode == AuthMode.LOGIN) "Ingresar" else "Registrarse")
        }

        TextButton(onClick = vm::onToggleMode) {
            Text(if (state.mode == AuthMode.LOGIN) "¿No tienes cuenta? Regístrate" else "¿Ya tienes cuenta? Ingresa")
        }
    }
}
