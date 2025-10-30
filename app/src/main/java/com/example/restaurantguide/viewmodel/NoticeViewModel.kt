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
    private val repo = NoticeRepository(AppDatabase.get(app).noticeDao())

    private val all: StateFlow<List<Notice>> =
        repo.all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _ui = MutableStateFlow(NoticeUiState())
    val ui: StateFlow<NoticeUiState> = _ui.asStateFlow()

    init {
        // Seed simple si no hay avisos
        viewModelScope.launch {
            if (all.value.isEmpty()) {
                listOf(
                    Notice(restaurantId = null, type="PROMO", title="La Terraza", summary="20% de descuento este fin de semana.", priority = 2),
                    Notice(restaurantId = null, type="NEWS",  title="Il Forno", summary="Menú de temporada disponible.", priority = 1),
                    Notice(restaurantId = null, type="EVENT", title="Sushi Zen", summary="Noche de ramen el viernes.", priority = 1)
                ).forEach { repo.upsert(it) }
            }
        }

        // Mantener lista filtrada por tipo + búsqueda, orden por prioridad desc y luego id desc
        viewModelScope.launch {
            combine(all, _ui) { list, ui ->
                val filtered = list
                    .asSequence()
                    .filter { ui.type == "Todos" || it.type.equals(ui.type, true) }
                    .filter { ui.query.isBlank() ||
                            it.title.contains(ui.query, true) ||
                            it.summary.contains(ui.query, true) }
                    .sortedWith(compareByDescending<Notice> { it.priority }
                        .thenByDescending { it.id })
                    .toList()
                ui.copy(items = filtered)
            }.collect { _ui.value = it }
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
}
