package com.example.restaurantguide.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.restaurantguide.data.model.Restaurant
import com.example.restaurantguide.viewmodel.RestaurantViewModel
import coil.compose.AsyncImage
import java.io.File
import com.google.android.gms.location.LocationServices
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun AddRestaurantScreen(
    vm: RestaurantViewModel,
    ownerId: Long,
    existingRestaurant: Restaurant? = null,
    onSaved: () -> Unit
) {
    var name by remember { mutableStateOf(existingRestaurant?.name ?: "") }
    var cuisine by remember { mutableStateOf(existingRestaurant?.cuisine ?: "") }
    var priceLevel by remember { mutableFloatStateOf(existingRestaurant?.priceLevel?.toFloat() ?: 2f) } // Slider needs float
    var address by remember { mutableStateOf(existingRestaurant?.address ?: "") }
    
    // Schedule state - Start and End time
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("22:00") }
    // Initialize from existing if possible
    LaunchedEffect(existingRestaurant) {
        if (existingRestaurant != null && existingRestaurant.schedule.contains("-")) {
            val parts = existingRestaurant.schedule.split("-").map { it.trim() }
            if (parts.size == 2) {
                startTime = parts[0]
                endTime = parts[1]
            }
        }
    }
    
    var description by remember { mutableStateOf(existingRestaurant?.description ?: "") }
    var imageUrlsString by remember { mutableStateOf(existingRestaurant?.imageUrls ?: "") }
    
    // Coordinates
    var latitude by remember { mutableStateOf(existingRestaurant?.latitude ?: 0.0) }
    var longitude by remember { mutableStateOf(existingRestaurant?.longitude ?: 0.0) }

    // Parse initial list
    var imagesList by remember { mutableStateOf(
        if (imageUrlsString.isBlank()) emptyList<String>() 
        else imageUrlsString.split(";").filter { it.isNotBlank() }
    )}
     LaunchedEffect(imagesList) { imageUrlsString = imagesList.joinToString(";") }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    
    // Time Pickers
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val initialTime = Calendar.getInstance()
    val startPickerState = rememberTimePickerState(initialHour = 9, initialMinute = 0, is24Hour = true)
    val endPickerState = rememberTimePickerState(initialHour = 22, initialMinute = 0, is24Hour = true)

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // --- Launchers (Same as before) ---
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
             fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                 if (loc != null) {
                     latitude = loc.latitude
                     longitude = loc.longitude
                     Toast.makeText(context, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                 } else {
                     Toast.makeText(context, "GPS desactivado?", Toast.LENGTH_LONG).show()
                 }
             }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploading = true
            vm.uploadImage(uri) { url ->
                isUploading = false
                if (url != null) imagesList = imagesList + url
            }
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            isUploading = true
            vm.uploadImage(tempCameraUri!!) { url ->
                isUploading = false
                if (url != null) imagesList = imagesList + url
            }
        }
    }
    fun createTempPictureUri(): Uri {
        val tempFile = File.createTempFile("camera_img_", ".jpg", context.cacheDir).apply { createNewFile(); deleteOnExit() }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
             val uri = createTempPictureUri()
             tempCameraUri = uri
             cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara necesario", Toast.LENGTH_SHORT).show()
        }
    }

    // Time Picker Logic
    if (showStartTimePicker) {
        DatePickerDialogLikeWrapper(onDismiss = { showStartTimePicker = false }, onConfirm = {
            startTime = String.format("%02d:%02d", startPickerState.hour, startPickerState.minute)
            showStartTimePicker = false
        }) {
            TimePicker(state = startPickerState)
        }
    }
    if (showEndTimePicker) {
        DatePickerDialogLikeWrapper(onDismiss = { showEndTimePicker = false }, onConfirm = {
            endTime = String.format("%02d:%02d", endPickerState.hour, endPickerState.minute)
            showEndTimePicker = false
        }) {
            TimePicker(state = endPickerState)
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Agregar Foto") },
            text = {
                Column {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) { Text("Galería") }
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        val permission = Manifest.permission.CAMERA
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            val uri = createTempPictureUri()
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermissionLauncher.launch(permission)
                        }
                    }) { Text("Cámara") }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showImageSourceDialog = false }) { Text("Cancelar") } }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(if (existingRestaurant == null) "Nuevo Restaurante" else "Editar Restaurante", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = cuisine, onValueChange = { cuisine = it }, label = { Text("Tipo de cocina") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        
        // PRICE SLIDER
        Text("Nivel de Precio: ${priceLevel.toInt()}", style = MaterialTheme.typography.titleSmall)
        Slider(
            value = priceLevel,
            onValueChange = { priceLevel = it },
            valueRange = 1f..4f,
            steps = 2, // Steps between 1 and 4 -> 2, 3 (2 steps)
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$", style = MaterialTheme.typography.bodySmall)
            Text("$$$$", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        
        // UBICACION
        Text("Ubicación GPS (Opcional)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Lat: $latitude", style = MaterialTheme.typography.bodySmall)
                Text("Lng: $longitude", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = {
                val perm = Manifest.permission.ACCESS_FINE_LOCATION
                if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                        if (loc != null) {
                            latitude = loc.latitude
                            longitude = loc.longitude
                        } else {
                             Toast.makeText(context, "No se pudo obtener ubicación. Abre Maps para calibrar.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    locationPermissionLauncher.launch(perm)
                }
            }) {
                Text("Obtener Ubicación")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // SCHEDULE PICKER
        Text("Horario de Atención", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { showStartTimePicker = true }, modifier = Modifier.weight(1f)) {
                Text("Abre: $startTime")
            }
            OutlinedButton(onClick = { showEndTimePicker = true }, modifier = Modifier.weight(1f)) {
                Text("Cierra: $endTime")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(modifier = Modifier.height(16.dp))
        
        // --- Image Section ---
        Text("Fotos (Toca 'X' para borrar)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        if (imagesList.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(100.dp).fillMaxWidth()) {
                items(imagesList) { url ->
                     Box(modifier = Modifier.size(100.dp)) {
                         AsyncImage(model = url, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                         // Delete button overlay
                         IconButton(
                             onClick = { 
                                 vm.deleteImage(url)
                                 imagesList = imagesList - url 
                             },
                             modifier = Modifier.align(Alignment.TopEnd).padding(2.dp).size(24.dp).background(Color.White.copy(alpha=0.7f), CircleShape)
                         ) {
                             Icon(Icons.Default.Close, contentDescription = "Borrar", tint = Color.Red, modifier = Modifier.padding(2.dp))
                         }
                     }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text("Subiendo...", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = { showImageSourceDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Agregar Foto") }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val r = Restaurant(
                    id = existingRestaurant?.id ?: 0,
                    name = name,
                    cuisine = cuisine,
                    priceLevel = priceLevel.toInt(),
                    address = address,
                    schedule = "$startTime - $endTime",
                    rating = existingRestaurant?.rating ?: 0.0,
                    description = description,
                    imageUrls = imageUrlsString,
                    ownerId = ownerId,
                    latitude = latitude,
                    longitude = longitude
                )
                vm.addRestaurant(r)
                onSaved()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading
        ) {
            Text("Guardar Restaurante")
        }
    }
}

@Composable
fun DatePickerDialogLikeWrapper(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onConfirm) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        text = { Column { content() } }
    )
}
