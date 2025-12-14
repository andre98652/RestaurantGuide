package com.example.restaurantguide.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantguide.data.database.AppDatabase
import com.example.restaurantguide.data.model.Notice
import com.example.restaurantguide.repository.NoticeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class NoticeUiState(
    val query: String = "",
    val type: String = "Todos", // PROMO / NEWS / EVENT
    val items: List<Notice> = emptyList()
)

class NoticeViewModel(app: Application) : AndroidViewModel(app) {
    // Repositorios: necesitamos acceso a Avisos, pero también a Restaurantes (para ver cuáles son favoritos) y Preferencias (para saber quién eres)
    // Repositorios: necesitamos acceso a Avisos, pero también a Restaurantes (para ver cuáles son favoritos) y Preferencias (para saber quién eres)
    private val db = com.example.restaurantguide.data.database.AppDatabase.get(app)
    private val repo = NoticeRepository(db)
    private val rRepo = com.example.restaurantguide.repository.RestaurantRepository(db)
    private val userPrefs = com.example.restaurantguide.data.prefs.UserPreferences(app)
    
    // We need user ID to know which favorites to check
    private val userId = userPrefs.profile.map { it.id }.stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    private val all: StateFlow<List<Notice>> =
        repo.all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _ui = MutableStateFlow(NoticeUiState())
    val ui: StateFlow<NoticeUiState> = _ui.asStateFlow()

    init {
        // LÓGICA DE NOTIFICACIONES:
        // Escucha la tubería de 'all' (todos los avisos). Si llega uno nuevo y es de un restaurante favorito -> Notificación.
        viewModelScope.launch {
            // Wait for initial load
            val initial = all.filter { it.isNotEmpty() }.first()
            var maxId = initial.maxOfOrNull { it.id } ?: System.currentTimeMillis()
            
            // Startup Check: Did we miss any recent notice (e.g. last 15 mins) while logged out?
            // Only for USER role
            val uid = userId.value
            if (uid != 0L) {
                 val role = userPrefs.profile.first().role
                 if (role == "USER") {
                     val recentThreshold = System.currentTimeMillis() - (15 * 60 * 1000)
                     val recentNotices = initial.filter { it.id > recentThreshold }
                     
                     if (recentNotices.isNotEmpty()) {
                         val favs = rRepo.getFavoritesForUser(uid).first()
                         recentNotices.forEach { n ->
                             if (n.restaurantId in favs) {
                                 val r = rRepo.getById(n.restaurantId!!)
                                 val rName = r?.name ?: "Restaurante"
                                 com.example.restaurantguide.utils.NotificationHelper.showNotification(
                                     getApplication(),
                                     "¡Oferta Reciente en $rName!",
                                     "${n.title}: ${n.summary}"
                                 )
                             }
                         }
                     }
                 }
            }
            
            all.collect { notices ->
                val newNotices = notices.filter { it.id > maxId }
                if (newNotices.isNotEmpty()) {
                    maxId = newNotices.maxOf { it.id }
                    
                    // Check if any new notice is from a favorite restaurant
                    val uid = userId.value
                    if (uid != 0L) {
                         // Check ROLE (Only notify if I am a USER, not the RESTAURANT owner themselves?)
                         // User request: "only in user accounts"
                         val role = userPrefs.profile.first().role
                         if (role == "USER") {
                             val favs = rRepo.getFavoritesForUser(uid).first()
                             
                             newNotices.forEach { n ->
                                 if (n.restaurantId in favs) {
                                     // Fetch restaurant name for nice notification
                                     val r = rRepo.getById(n.restaurantId!!)
                                     val rName = r?.name ?: "Restaurante"
                                     com.example.restaurantguide.utils.NotificationHelper.showNotification(
                                         getApplication(),
                                         "¡Oferta en $rName!",
                                         "${n.title}: ${n.summary}"
                                     )
                                 }
                             }
                         }
                    }
                }
            }
        }

        // MANTENER LA LISTA FILTRADA:
        // Combina la lista de avisos (all) con los filtros de la interfaz (_ui)
        viewModelScope.launch {
            combine(all, _ui) { list, ui ->
                val filtered = list
                    .asSequence()
                    .filter { ui.type == "Todos" || it.type.equals(ui.type, true) }
                    .filter { ui.query.isBlank() ||
                            it.title.contains(ui.query, true) ||
                            it.summary.contains(ui.query, true) }
                    // Filter expired notices
                    .filter { it.endAt == null || it.endAt > System.currentTimeMillis() }
                    .sortedWith(compareByDescending<Notice> { it.priority }
                        .thenByDescending { it.id })
                    .toList()
                if (ui.items != filtered) {
                    ui.copy(items = filtered)
                } else ui
            }.collect { 
                 if (it != _ui.value) _ui.value = it
            }
        }
    }

    fun setQuery(q: String) = _ui.update { it.copy(query = q) }
    fun setType(t: String) = _ui.update { it.copy(type = t) }

    fun markRead(id: Long) {
        viewModelScope.launch { repo.markRead(id) }
    }

    fun markAllRead() {
        viewModelScope.launch { repo.markAllRead() }
    }

    // Publicar un aviso nuevo (solo para dueños)
    fun addNotice(ownerId: Long, title: String, summary: String, type: String, hoursInDuration: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val restaurants = rRepo.getByOwner(ownerId)
            if (restaurants.isNotEmpty()) {
                val r = restaurants.first() // Pick first one for now
                val now = System.currentTimeMillis()
                val endAt = if (hoursInDuration > 0) now + (hoursInDuration * 3600 * 1000L) else null
                
                val n = Notice(
                    restaurantId = r.id,
                    type = type,
                    title = title,
                    summary = summary,
                    startAt = now,
                    endAt = endAt,
                    priority = 0 // Default priority
                )
                repo.upsert(n)
                // We do NOT notify here manually anymore. 
                // The loop above will detect the new notice (even on this device) and notify.
                // If we want to suppress notification for the creator, we can check ownerId.
                // But user requirement was "notify user". Creator is also a user? 
                // Usually creator doesn't need notif.
                // But for "testing with 1 device" it was useful. 
                // Now we support multi-device.
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }
}
