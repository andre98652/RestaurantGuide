package com.example.restaurantguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.restaurantguide.ui.AppScaffold
import com.example.restaurantguide.viewmodel.NoticeViewModel
import com.example.restaurantguide.viewmodel.RestaurantViewModel
import com.example.restaurantguide.viewmodel.AuthViewModel
import com.example.restaurantguide.ui.theme.RestaurantGuideTheme
import android.Manifest
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import com.example.restaurantguide.utils.NotificationHelper

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        NotificationHelper.createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            RestaurantGuideTheme {
                val vm: RestaurantViewModel = viewModel()
                val nm: NoticeViewModel = viewModel()
                val authVm: AuthViewModel = viewModel()
                AppScaffold(vm = vm, nm = nm, authVm = authVm)
            }
        }
    }
}
