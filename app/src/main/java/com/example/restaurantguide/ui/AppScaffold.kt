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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.restaurantguide.ui.components.AppTopBar

@Composable
fun AppScaffold(
    vm: RestaurantViewModel = viewModel(),
    nm: NoticeViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.HOME

    // Mapea título y si muestra “volver”
    val (title, showBack) = when {
        currentRoute.startsWith(Routes.DETAIL.substringBefore("/{")) -> "Detalle" to true
        currentRoute.startsWith(Routes.CATEGORY.substringBefore("/{")) -> "Categoría" to true
        currentRoute == Routes.FAVORITES -> "Favoritos" to false
        currentRoute == Routes.NOTICES -> "Avisos" to false
        currentRoute == Routes.PROFILE -> "Perfil" to false
        else -> "Restaurantes" to false // HOME
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                showBack = showBack,
                onBack = { navController.popBackStack() }
            )
        },
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
