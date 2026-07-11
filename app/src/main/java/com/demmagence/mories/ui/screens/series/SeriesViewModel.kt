package com.demmagence.mories.ui.screens.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.demmagence.mories.data.paging.TvPagingSource
import com.demmagence.mories.data.remote.TmdbApiService
import com.demmagence.mories.data.remote.dto.toDomain
import com.demmagence.mories.domain.model.Genre
import com.demmagence.mories.domain.repository.TvRepository
import com.demmagence.mories.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val tvRepository: TvRepository,
    private val api: TmdbApiService
) : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _selectedGenreId = MutableStateFlow<Int?>(null)
    val selectedGenreId: StateFlow<Int?> = _selectedGenreId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tvShows = _selectedGenreId.flatMapLatest { genreId ->
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = {
                TvPagingSource(api) { apiService, page ->
                    if (genreId != null) {
                        apiService.discoverTvShows(genreId, page).results.map { it.toDomain() }
                    } else {
                        apiService.getPopularTvShows(page).results.map { it.toDomain() }
                    }
                }
            }
        ).flow
    }.cachedIn(viewModelScope)

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            when (val result = tvRepository.getTvGenres()) {
                is Resource.Success -> _genres.value = result.data
                else -> {}
            }
        }
    }

    fun selectGenre(genreId: Int?) {
        _selectedGenreId.value = if (_selectedGenreId.value == genreId) null else genreId
    }
}
