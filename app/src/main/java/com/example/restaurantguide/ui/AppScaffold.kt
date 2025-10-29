package com.example.restaurantguide.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantguide.ui.components.BottomBar
import com.example.restaurantguide.viewmodel.NoticeViewModel
import com.example.restaurantguide.viewmodel.RestaurantViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

@Composable
fun AppScaffold(
    vm: RestaurantViewModel = viewModel(),
    nm: NoticeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    Scaffold(
        bottomBar = {
            BottomBar(
                current = currentRoute,
                onHome = {
                    if (currentRoute != Routes.HOME) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                onFavorites = {
                    if (currentRoute != Routes.FAVORITES) {
                        navController.navigate(Routes.FAVORITES) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                onNotices = {
                    if (currentRoute != Routes.NOTICES) {
                        navController.navigate(Routes.NOTICES) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                onProfile = {
                    if (currentRoute != Routes.PROFILE) {
                        navController.navigate(Routes.PROFILE) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    ) { padding ->
        AppNav(
            vm = vm,
            nm = nm,
            navController = navController,   // 👈 usamos el MISMO navController
            modifier = androidx.compose.ui.Modifier.padding(padding)
        )
    }
}
