package com.demmagence.mories.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.demmagence.mories.data.paging.SearchPagingSource
import com.demmagence.mories.data.remote.TmdbApiService
import com.demmagence.mories.domain.model.Genre
import com.demmagence.mories.domain.model.Movie
import com.demmagence.mories.domain.repository.MovieRepository
import com.demmagence.mories.domain.repository.TvRepository
import com.demmagence.mories.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val tvRepository: TvRepository,
    private val api: TmdbApiService
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("all") // "all", "movie", "tv"
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _debouncedQuery = MutableStateFlow("")
    val debouncedQuery: StateFlow<String> = _debouncedQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults = combine(
        _searchQuery.debounce(500),
        _selectedFilter,
        _genres
    ) { query, filter, genresList ->
        _debouncedQuery.value = query
        Triple(query, filter, genresList)
    }.flatMapLatest { (query, filter, genresList) ->
        if (query.isBlank()) {
            flowOf(PagingData.empty())
        } else {
            Pager(
                config = PagingConfig(pageSize = 20, prefetchDistance = 5),
                pagingSourceFactory = {
                    SearchPagingSource(api, query, filter, genresList)
                }
            ).flow
        }
    }.cachedIn(viewModelScope)

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            when (val result = movieRepository.getMovieGenres()) {
                is Resource.Success -> _genres.value = result.data
                else -> {}
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: String) {
        _selectedFilter.value = filter
    }
}
