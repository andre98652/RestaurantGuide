package com.example.restaurantguide.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
    // REPOSITORIO: El "Jefe de Cocina" que nos da los datos.
    private val repo = RestaurantRepository()
    private val userPrefs = com.example.restaurantguide.data.prefs.UserPreferences(app)
    
    val userId = userPrefs.profile.map { it.id }.stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    // FLUJO DE TODOS LOS RESTAURANTES:
    // Esta lista se actualiza sola en tiempo real desde Firebase.
    val all: StateFlow<List<Restaurant>> =
        repo.restaurants.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        
    // Track the restaurant owned by the current user
    val myRestaurant: StateFlow<Restaurant?> = combine(all, userId) { list, uid ->
        if (uid == 0L) null else list.find { it.ownerId == uid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _home = MutableStateFlow(HomeUiState())
    val home: StateFlow<HomeUiState> = _home.asStateFlow()
    
    init {
        // Seeding checked inside logic or backend, skipping explicit seed check in ViewModel for simplicity or re-add
        // For migration, we rely on seeded data in Firestore or manual seed via AddRestaurant.
        // If we want seed, we can check repo.count() or similar. 
        // repo.restaurants is a flow, so we don't check count synchronously.
        // Skipping seed logic for now as user likely verified setup.

        // LÓGICA DE FILTRADO (Buscador y Categorías):
        // Combina la lista completa (all) con el estado de la UI (_home) para filtrar.
        viewModelScope.launch {
            combine(all, _home) { list, ui ->
                val filtered = list.filter { r ->
                    (ui.selectedCuisine == "Todos" || r.cuisine.equals(ui.selectedCuisine, true)) &&
                            (ui.query.isBlank() || r.name.contains(ui.query, true) || r.cuisine.contains(ui.query, true))
                }
                ui.copy(items = filtered)
            }.collect { 
                // We must update the MutableStateFlow, but 'combine' produces a new HomeUiState.
                // We need to be careful not to infinite loop if we observe _home.
                // Actually the pattern was: combine(all, _home) collected into _home. This works if we only update 'items'.
                if (it.items != _home.value.items) {
                     _home.value = it
                }
            }
        }
    }

    // Funciones para que la Vista avise que el usuario escribió algo en el buscador
    fun updateQuery(q: String) { _home.update { it.copy(query = q) } }
    fun selectCuisine(c: String) { _home.update { it.copy(selectedCuisine = c) } }

    // FAVORITOS:
    // Detecta quién es el usuario y pide su lista de restaurantes favoritos.
    val favorites: StateFlow<List<Restaurant>> = userId.flatMapLatest { uid ->
        if (uid != 0L) {
             // Get list of favorite IDs, then map to Restaurants
             repo.getFavoritesForUser(uid).map { ids ->
                 all.value.filter { it.id in ids }
             }
        } else {
            flowOf(emptyList()) 
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun byCuisineFlow(c: String): StateFlow<List<Restaurant>> =
        all.map { list -> list.filter { it.cuisine.equals(c, true) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getByIdSuspend(id: Long, onResult: (Restaurant?) -> Unit) {
        viewModelScope.launch { onResult(repo.getById(id)) }
    }

    // Acción de dar Like/Dislike
    fun toggleFavorite(id: Long) {
        val uid = userId.value
        if (uid != 0L) {
            viewModelScope.launch { repo.toggleFavorite(uid, id) }
        }
    }

    fun addRestaurant(r: Restaurant) {
        viewModelScope.launch { repo.upsert(r) }
    }
    
    fun getReviews(id: Long) = flow {
         // Transform flow<List<Review>> to flow
         repo.getReviewsForRestaurant(id).collect { emit(it) }
    }
    
    // Agregar una Reseña nueva (solo si estás logueado)
    fun addReview(restaurantId: Long, comment: String, rating: Int) {
        val uid = userId.value
        if (uid == 0L) return
        viewModelScope.launch {
            val name = userPrefs.profile.first().name
            repo.addReview(
                com.example.restaurantguide.data.model.Review(
                    userId = uid,
                    userName = name,
                    restaurantId = restaurantId,
                    comment = comment,
                    rating = rating
                )
            )
            
            // Recalculate average
            val reviews = repo.getReviewsForRestaurant(restaurantId).first() // Wait for list
            if (reviews.isNotEmpty()) {
                val avg = reviews.map { it.rating }.average()
                val r = repo.getById(restaurantId)
                if (r != null) {
                    val updated = r.copy(rating = String.format("%.1f", avg).replace(',','.').toDouble())
                    repo.upsert(updated)
                }
            }
        }
    }

    fun uploadImage(uri: android.net.Uri, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Generate a unique path: restaurants/{timestamp}_{random}.jpg
                val path = "restaurants/${System.currentTimeMillis()}_${(1..1000).random()}.jpg"
                val url = repo.uploadImage(uri, path)
                onResult(url)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    fun deleteImage(url: String) {
        viewModelScope.launch {
            repo.deleteImage(url)
        }
    }
}
