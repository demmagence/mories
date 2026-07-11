package com.demmagence.mories.ui.screens.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.demmagence.mories.data.paging.MoviePagingSource
import com.demmagence.mories.data.paging.TvPagingSource
import com.demmagence.mories.data.remote.TmdbApiService
import com.demmagence.mories.data.remote.dto.toDomain
import com.demmagence.mories.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import androidx.paging.PagingData
import androidx.paging.map

@HiltViewModel
class GenreListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val api: TmdbApiService
) : ViewModel() {

    val genreId: Int = savedStateHandle["genreId"] ?: 0
    val genreName: String = savedStateHandle["genreName"] ?: ""
    val mediaType: String = savedStateHandle["mediaType"] ?: "movie"

    val items = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = {
            if (mediaType == "movie") {
                MoviePagingSource(api) { apiService, page ->
                    apiService.discoverMovies(genreId, page).results.map { it.toDomain() }
                }
            } else {
                MoviePagingSource(api) { apiService, page ->
                    apiService.discoverTvShows(genreId, page).results.map {
                        Movie(
                            id = it.id,
                            title = it.name ?: "",
                            overview = it.overview ?: "",
                            posterPath = it.posterPath,
                            backdropPath = it.backdropPath,
                            voteAverage = it.voteAverage ?: 0.0,
                            voteCount = it.voteCount ?: 0,
                            releaseDate = it.firstAirDate,
                            mediaType = "tv"
                        )
                    }
                }
            }
        }
    ).flow.cachedIn(viewModelScope)
}
