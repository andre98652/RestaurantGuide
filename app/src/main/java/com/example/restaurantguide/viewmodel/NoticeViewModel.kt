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

class NoticeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = NoticeRepository(AppDatabase.get(app).noticeDao())

    val notices: StateFlow<List<Notice>> =
        repo.all().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Seed simple de avisos (demo)
        viewModelScope.launch {
            if (notices.value.isEmpty()) {
                listOf(
                    Notice(restaurantId = null, type="PROMO", title="La Terraza", summary="20% de descuento este fin de semana."),
                    Notice(restaurantId = null, type="NEWS",  title="Il Forno", summary="Menú de temporada disponible."),
                    Notice(restaurantId = null, type="EVENT", title="Sushi Zen", summary="Noche de ramen el viernes.")
                ).forEach { repo.upsert(it) }
            }
        }
    }
}
