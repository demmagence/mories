package com.demmagence.mories.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demmagence.mories.domain.model.Movie
import com.demmagence.mories.domain.model.TvShow
import com.demmagence.mories.domain.repository.MovieRepository
import com.demmagence.mories.domain.repository.TvRepository
import com.demmagence.mories.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val trendingMovies: List<Movie> = emptyList(),
    val trendingTvShows: List<TvShow> = emptyList(),
    val popularMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val nowPlayingMovies: List<Movie> = emptyList(),
    val popularTvShows: List<TvShow> = emptyList(),
    val topRatedTvShows: List<TvShow> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val trendingMoviesDeferred = async { movieRepository.getTrendingMovies() }
            val trendingTvDeferred = async { tvRepository.getTrendingTvShows() }
            val popularMoviesDeferred = async { movieRepository.getPopularMovies(1) }
            val topRatedMoviesDeferred = async { movieRepository.getTopRatedMovies(1) }
            val upcomingMoviesDeferred = async { movieRepository.getUpcomingMovies(1) }
            val nowPlayingMoviesDeferred = async { movieRepository.getNowPlayingMovies(1) }
            val popularTvDeferred = async { tvRepository.getPopularTvShows(1) }
            val topRatedTvDeferred = async { tvRepository.getTopRatedTvShows(1) }

            val trendingMovies = trendingMoviesDeferred.await()
            val trendingTv = trendingTvDeferred.await()
            val popularMovies = popularMoviesDeferred.await()
            val topRatedMovies = topRatedMoviesDeferred.await()
            val upcomingMovies = upcomingMoviesDeferred.await()
            val nowPlayingMovies = nowPlayingMoviesDeferred.await()
            val popularTv = popularTvDeferred.await()
            val topRatedTv = topRatedTvDeferred.await()

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    isRefreshing = false,
                    trendingMovies = (trendingMovies as? Resource.Success)?.data ?: emptyList(),
                    trendingTvShows = (trendingTv as? Resource.Success)?.data ?: emptyList(),
                    popularMovies = (popularMovies as? Resource.Success)?.data ?: emptyList(),
                    topRatedMovies = (topRatedMovies as? Resource.Success)?.data ?: emptyList(),
                    upcomingMovies = (upcomingMovies as? Resource.Success)?.data ?: emptyList(),
                    nowPlayingMovies = (nowPlayingMovies as? Resource.Success)?.data ?: emptyList(),
                    popularTvShows = (popularTv as? Resource.Success)?.data ?: emptyList(),
                    topRatedTvShows = (topRatedTv as? Resource.Success)?.data ?: emptyList(),
                    error = if (trendingMovies is Resource.Error && popularMovies is Resource.Error) {
                        (trendingMovies as Resource.Error).message
                    } else null
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            loadHomeData()
        }
    }
}
