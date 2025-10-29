package com.example.restaurantguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.restaurantguide.ui.AppScaffold
import com.example.restaurantguide.viewmodel.NoticeViewModel
import com.example.restaurantguide.viewmodel.RestaurantViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                val vm: RestaurantViewModel = viewModel()
                val nm: NoticeViewModel = viewModel()
                AppScaffold(vm = vm, nm = nm)
            }
        }
    }
}
