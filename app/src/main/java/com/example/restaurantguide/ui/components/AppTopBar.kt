package com.example.restaurantguide.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.restaurantguide.ui.theme.RedPrimary
import com.example.restaurantguide.ui.theme.OnPrimary
import androidx.compose.material3.ExperimentalMaterial3Api


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = { Text(title, color = OnPrimary) },
        navigationIcon = {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = OnPrimary)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = RedPrimary,
            titleContentColor = OnPrimary,
            navigationIconContentColor = OnPrimary
        )
    )
}
