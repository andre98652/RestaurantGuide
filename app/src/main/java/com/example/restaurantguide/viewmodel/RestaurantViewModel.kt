package com.example.restaurantguide.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantguide.data.database.AppDatabase
import com.example.restaurantguide.data.model.Restaurant
import com.example.restaurantguide.repository.RestaurantRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val query: String = "",
    val selectedCuisine: String = "Todos",
    val items: List<Restaurant> = emptyList()
)

class RestaurantViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RestaurantRepository(AppDatabase.get(app).restaurantDao())

    val all: StateFlow<List<Restaurant>> =
        repo.all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _home = MutableStateFlow(HomeUiState())
    val home: StateFlow<HomeUiState> = _home.asStateFlow()
    private var seeded = false
    init {
        viewModelScope.launch {
            val count = repo.count()
            if (count == 0) {
                val seed = listOf(
                    Restaurant(name="La Terraza", cuisine="Peruana", priceLevel=2, address="Av. Principal 123",
                        schedule="13:00 - 23:30", rating=4.5, description="Vista panorámica."),
                    Restaurant(name="Il Forno", cuisine="Italiana", priceLevel=3, address="Calle Roma 456",
                        schedule="13:00 - 23:30", rating=4.8, description="Pastas a la leña."),
                    Restaurant(name="Sushi Zen", cuisine="Japonesa", priceLevel=2, address="Jr. Sakura 77",
                        schedule="12:00 - 22:00", rating=4.3, description="Makis y ramen."),
                    Restaurant(name="Grill & Bar", cuisine="Parrillas", priceLevel=3, address="Jr. Asador 12",
                        schedule="12:00 - 23:00", rating=4.4, description="Parrillas y cortes.")
                )
                seed.forEach { repo.upsert(it) }
            }
        }

        // Mantener items filtrados en Home
        viewModelScope.launch {
            combine(all, _home) { list, ui ->
                val filtered = list.filter { r ->
                    (ui.selectedCuisine == "Todos" || r.cuisine.equals(ui.selectedCuisine, true)) &&
                            (ui.query.isBlank() || r.name.contains(ui.query, true) || r.cuisine.contains(ui.query, true))
                }
                ui.copy(items = filtered)
            }.collect { _home.value = it }
        }
    }


    fun updateQuery(q: String) { _home.update { it.copy(query = q) } }
    fun selectCuisine(c: String) { _home.update { it.copy(selectedCuisine = c) } }

    //fun favorites(): StateFlow<List<Restaurant>> =
    //    repo.favorites().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val favorites: StateFlow<List<Restaurant>> =
        repo.favorites().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun byCuisineFlow(c: String): StateFlow<List<Restaurant>> =
        repo.byCuisine(c).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getByIdSuspend(id: Long, onResult: (Restaurant?) -> Unit) {
        viewModelScope.launch { onResult(repo.getById(id)) }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch { repo.toggleFavorite(id) }
    }
}
