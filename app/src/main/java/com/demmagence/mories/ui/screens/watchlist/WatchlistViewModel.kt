package com.demmagence.mories.ui.screens.watchlist

import androidx.lifecycle.ViewModel
import com.demmagence.mories.domain.model.WatchlistItem
import com.demmagence.mories.domain.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    val watchlistItems: Flow<List<WatchlistItem>> = watchlistRepository.getAllWatchlistItems()

    fun removeFromWatchlist(id: Int, mediaType: String) {
        viewModelScope.launch {
            watchlistRepository.removeFromWatchlist(id, mediaType)
        }
    }
}
