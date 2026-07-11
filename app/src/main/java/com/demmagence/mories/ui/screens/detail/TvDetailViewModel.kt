package com.demmagence.mories.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demmagence.mories.domain.model.Episode
import com.demmagence.mories.domain.model.TvShowDetail
import com.demmagence.mories.domain.model.WatchlistItem
import com.demmagence.mories.domain.repository.TvRepository
import com.demmagence.mories.domain.repository.WatchlistRepository
import com.demmagence.mories.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TvDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val tvDetail: TvShowDetail? = null,
    val isInWatchlist: Boolean = false,
    val selectedSeason: Int = 1,
    val episodes: List<Episode> = emptyList(),
    val isLoadingEpisodes: Boolean = false
)

@HiltViewModel
class TvDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tvRepository: TvRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val tvId: Int = savedStateHandle["tvId"] ?: 0

    private val _uiState = MutableStateFlow(TvDetailUiState())
    val uiState: StateFlow<TvDetailUiState> = _uiState.asStateFlow()

    init {
        loadTvDetail()
        checkWatchlist()
    }

    private fun loadTvDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = tvRepository.getTvShowDetails(tvId)) {
                is Resource.Success -> {
                    val detail = result.data
                    val firstSeason = detail.seasons.firstOrNull { it.seasonNumber > 0 }?.seasonNumber ?: 1
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tvDetail = detail,
                            selectedSeason = firstSeason
                        )
                    }
                    loadEpisodes(firstSeason)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun selectSeason(seasonNumber: Int) {
        _uiState.update { it.copy(selectedSeason = seasonNumber) }
        loadEpisodes(seasonNumber)
    }

    private fun loadEpisodes(seasonNumber: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEpisodes = true) }
            when (val result = tvRepository.getTvSeasonEpisodes(tvId, seasonNumber)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoadingEpisodes = false, episodes = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoadingEpisodes = false) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun checkWatchlist() {
        viewModelScope.launch {
            val isInWatchlist = watchlistRepository.isInWatchlist(tvId, "tv")
            _uiState.update { it.copy(isInWatchlist = isInWatchlist) }
        }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            val detail = _uiState.value.tvDetail ?: return@launch
            if (_uiState.value.isInWatchlist) {
                watchlistRepository.removeFromWatchlist(tvId, "tv")
                _uiState.update { it.copy(isInWatchlist = false) }
            } else {
                watchlistRepository.addToWatchlist(
                    WatchlistItem(
                        id = detail.id,
                        title = detail.name,
                        posterPath = detail.posterPath,
                        mediaType = "tv",
                        voteAverage = detail.voteAverage,
                        addedAt = System.currentTimeMillis()
                    )
                )
                _uiState.update { it.copy(isInWatchlist = true) }
            }
        }
    }

    fun retry() {
        loadTvDetail()
    }
}
