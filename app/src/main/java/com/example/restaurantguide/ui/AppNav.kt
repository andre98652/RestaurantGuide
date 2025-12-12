package com.example.restaurantguide.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.LaunchedEffect
import com.example.restaurantguide.ui.screens.*
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.restaurantguide.ui.screens.AddRestaurantScreen
import com.example.restaurantguide.viewmodel.AuthViewModel
import com.example.restaurantguide.viewmodel.NoticeViewModel
import com.example.restaurantguide.viewmodel.RestaurantViewModel

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val FAVORITES = "favorites"
    const val NOTICES = "notices"
    const val PROFILE = "profile"
    const val CATEGORY = "category/{name}"
    const val DETAIL = "detail/{id}"
    const val ADD_RESTAURANT = "add_restaurant"
    const val ADD_NOTICE = "add_notice"
}

@Composable
fun AppNav(
    vm: RestaurantViewModel,
    nm: NoticeViewModel,
    authVm: AuthViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN, // Iniciar en Login
        modifier = modifier
    ) {
        // Observador Global de Autenticación
        composable(Routes.LOGIN) {
            AuthScreen(vm = authVm) {
                // Al tener éxito -> Ir a Inicio, limpiar pila
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }
        composable(Routes.HOME) {
            val authState by authVm.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                vm = vm,
                userRole = authState.role,
                onOpenDetail = { id -> navController.navigate("detail/$id") },
                onOpenCategory = { name -> navController.navigate("category/$name") },
                onOpenFavorites = { navController.navigate(Routes.FAVORITES) },
                onOpenNotices = { navController.navigate(Routes.NOTICES) },
                onOpenProfile = { navController.navigate(Routes.PROFILE) },
                onAddRestaurant = { navController.navigate(Routes.ADD_RESTAURANT) }
            )
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(vm) { id -> navController.navigate("detail/$id") }
        }
        composable(Routes.NOTICES) {
            val authState by authVm.uiState.collectAsStateWithLifecycle()
            NoticesScreen(
                nm = vm.let { nm }, // nm passed in args
                userRole = authState.role,
                onOpenRestaurant = { restId -> restId?.let { navController.navigate("detail/$it") } },
                onAddNotice = { navController.navigate(Routes.ADD_NOTICE) }
            )
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
        composable(Routes.ADD_RESTAURANT) {
            val authState by authVm.uiState.collectAsStateWithLifecycle()
            val myRestaurant by vm.myRestaurant.collectAsStateWithLifecycle()
            AddRestaurantScreen(
                vm = vm,
                ownerId = authState.id,
                existingRestaurant = myRestaurant
            ) {
                navController.popBackStack()
            }
        }
        composable(Routes.ADD_NOTICE) {
            val authState by authVm.uiState.collectAsStateWithLifecycle()
            AddNoticeScreen(
                vm = nm,
                ownerId = authState.id,
                onSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
