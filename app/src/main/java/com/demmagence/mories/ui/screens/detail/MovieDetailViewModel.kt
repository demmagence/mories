package com.demmagence.mories.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demmagence.mories.domain.model.MovieDetail
import com.demmagence.mories.domain.repository.MovieRepository
import com.demmagence.mories.domain.repository.WatchlistRepository
import com.demmagence.mories.domain.model.WatchlistItem
import com.demmagence.mories.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val movieDetail: MovieDetail? = null,
    val isInWatchlist: Boolean = false
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val movieId: Int = savedStateHandle["movieId"] ?: 0

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
        checkWatchlist()
    }

    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = movieRepository.getMovieDetails(movieId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, movieDetail = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun checkWatchlist() {
        viewModelScope.launch {
            val isInWatchlist = watchlistRepository.isInWatchlist(movieId, "movie")
            _uiState.update { it.copy(isInWatchlist = isInWatchlist) }
        }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            val detail = _uiState.value.movieDetail ?: return@launch
            if (_uiState.value.isInWatchlist) {
                watchlistRepository.removeFromWatchlist(movieId, "movie")
                _uiState.update { it.copy(isInWatchlist = false) }
            } else {
                watchlistRepository.addToWatchlist(
                    WatchlistItem(
                        id = detail.id,
                        title = detail.title,
                        posterPath = detail.posterPath,
                        mediaType = "movie",
                        voteAverage = detail.voteAverage,
                        addedAt = System.currentTimeMillis()
                    )
                )
                _uiState.update { it.copy(isInWatchlist = true) }
            }
        }
    }

    fun retry() {
        loadMovieDetail()
    }
}
