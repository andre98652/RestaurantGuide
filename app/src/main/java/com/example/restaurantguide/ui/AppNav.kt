package com.example.restaurantguide.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.restaurantguide.ui.screens.*
import com.example.restaurantguide.viewmodel.NoticeViewModel
import com.example.restaurantguide.viewmodel.RestaurantViewModel

object Routes {
    const val HOME = "home"
    const val FAVORITES = "favorites"
    const val NOTICES = "notices"
    const val PROFILE = "profile"
    const val CATEGORY = "category/{name}"
    const val DETAIL = "detail/{id}"
}

@Composable
fun AppNav(
    vm: RestaurantViewModel,
    nm: NoticeViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                vm = vm,
                onOpenDetail = { id -> navController.navigate("detail/$id") },
                onOpenCategory = { name -> navController.navigate("category/$name") },
                onOpenFavorites = { navController.navigate(Routes.FAVORITES) },
                onOpenNotices = { navController.navigate(Routes.NOTICES) },
                onOpenProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(vm) { id -> navController.navigate("detail/$id") }
        }
        composable(Routes.NOTICES) {
            NoticesScreen(nm) { restId -> restId?.let { navController.navigate("detail/$it") } }
        }
        composable(Routes.PROFILE) {
            ProfileScreen()
        }
        composable(Routes.DETAIL) { backStack ->
            val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: -1
            DetailScreen(id = id, vm = vm) { navController.popBackStack() }
        }
        composable(Routes.CATEGORY) { backStack ->
            val name = backStack.arguments?.getString("name") ?: "Todos"
            CategoryScreen(category = name, vm = vm) { id -> navController.navigate("detail/$id") }
        }
    }
}
