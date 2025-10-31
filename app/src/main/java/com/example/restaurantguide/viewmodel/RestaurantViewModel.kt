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
    private val repo = RestaurantRepository(AppDatabase.get(app).restaurantDao()) //crear repo

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
                        schedule="13:00 - 23:30", rating=4.5, description="Vista panorámica.",
                        imageUrls = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/16/e8/98/e2/hermoza-vista-de-las.jpg?w=900&h=500&s=1;" +
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROJAZqJUzGiwN-L24W5-NARNHllb1Y8Id8zg&s;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/16/e8/99/44/tambien-somos-romanticos.jpg?w=900&h=500&s=1;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/16/e8/99/0b/de-dia-tambien-es-hermozo.jpg?w=900&h=500&s=1;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/16/e8/98/ee/pisco-sour-y-chilcanos.jpg?w=900&h=500&s=1"
                    ),
                    Restaurant(name="Il Forno", cuisine="Italiana", priceLevel=3, address="Calle Roma 456",
                        schedule="13:00 - 23:30", rating=4.8, description="Pastas a la leña.",
                        imageUrls = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1c/6a/b4/06/il-fornos-pizzeria-artesanal.jpg?w=900&h=500&s=1;" +
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROJAZqJUzGiwN-L24W5-NARNHllb1Y8Id8zg&s;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/21/71/39/06/pizza-barbecue.jpg?w=900&h=500&s=1;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1d/47/f1/27/pizza-pepperoni.jpg?w=900&h=500&s=1;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1d/28/81/04/pizza-nostra-la-de-la.jpg?w=700&h=400&s=1"
                    ),
                    Restaurant(name="Sushi Zen", cuisine="Japonesa", priceLevel=2, address="Jr. Sakura 77",
                        schedule="12:00 - 22:00", rating=4.3, description="Makis y ramen.",
                        imageUrls = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/07/de/de/c0/california-roll-excellents.jpg?w=1000&h=-1&s=1;" +
                        "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/0b/dd/1f/df/menu-japon.jpg?w=1000&h=-1&s=1;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1b/76/b9/b8/photo0jpg.jpg?w=1000&h=-1&s=1;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1a/24/34/b1/photo0jpg.jpg?w=1000&h=-1&s=1;"
                        +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/0a/d2/9f/f5/diner-sympa.jpg?w=1000&h=-1&s=1"
                    ),
                    Restaurant(name="Grill & Bar", cuisine="Parrillas", priceLevel=3, address="Jr. Asador 12",
                        schedule="12:00 - 23:00", rating=4.4, description="Parrillas y cortes.",
                        imageUrls = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1c/06/3b/e3/grill-ekeko-imperial.jpg?w=1000&h=-1&s=1;" +
                                "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1c/06/3b/fc/trio-real-con-papas-nativas.jpg?w=1000&h=-1&s=1;"
                                +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1c/06/3b/f1/piqueo-de-costilla-de.jpg?w=1000&h=-1&s=1;"
                                +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1c/06/3b/e5/grill-ekeko-anticuchero.jpg?w=1000&h=-1&s=1;"
                                +"https://dynamic-media-cdn.tripadvisor.com/media/photo-o/1c/06/3b/fe/costilla-bbq.jpg?w=1000&h=-1&s=1"
                    )
                )
                seed.forEach { repo.upsert(it) }
            }
        }

        // Mantener items filtrados en Home
        viewModelScope.launch {
            combine(all, _home) { list, ui ->
                val filtered = list.filter { r ->
                    //La cocina debe coincidir con el filtro seleccionado o el filtro debe ser “Todos”.
                    //La búsqueda (query) debe estar vacía o el nombre/cocina debe contener el texto buscado.
                    (ui.selectedCuisine == "Todos" || r.cuisine.equals(ui.selectedCuisine, true)) &&
                            (ui.query.isBlank() || r.name.contains(ui.query, true) || r.cuisine.contains(ui.query, true))
                }
                ui.copy(items = filtered)//Compose detecta nuevas referencias de estado y se recompone correctamente.
                                        //Mutar listas internas sin cambiar la referencia del estado no dispararía recomposición.
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
