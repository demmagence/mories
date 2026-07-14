package com.demmagence.mories.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.demmagence.mories.data.remote.TmdbApiService
import com.demmagence.mories.data.remote.dto.toMovie
import com.demmagence.mories.data.remote.dto.toDomain
import com.demmagence.mories.domain.model.Movie

class SearchPagingSource(
    private val api: TmdbApiService,
    private val query: String,
    private val mediaType: String, // "all", "movie", "tv"
    private val genres: List<com.demmagence.mories.domain.model.Genre> = emptyList()
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val matchedGenre = genres.find { it.name.equals(query, ignoreCase = true) }
            val results = if (matchedGenre != null) {
                val genreId = matchedGenre.id
                when (mediaType) {
                    "movie" -> {
                        api.discoverMovies(genreId, page).results.map { it.toDomain().copy(mediaType = "movie") }
                    }
                    "tv" -> {
                        api.discoverTvShows(genreId, page).results.map {
                            Movie(
                                id = it.id,
                                title = it.name ?: "",
                                overview = it.overview ?: "",
                                posterPath = it.posterPath,
                                backdropPath = it.backdropPath,
                                voteAverage = it.voteAverage ?: 0.0,
                                voteCount = it.voteCount ?: 0,
                                releaseDate = it.firstAirDate,
                                genreIds = it.genreIds ?: emptyList(),
                                mediaType = "tv"
                            )
                        }
                    }
                    else -> {
                        val movies = api.discoverMovies(genreId, page).results.map { it.toDomain().copy(mediaType = "movie") }
                        val tvs = api.discoverTvShows(genreId, page).results.map {
                            Movie(
                                id = it.id,
                                title = it.name ?: "",
                                overview = it.overview ?: "",
                                posterPath = it.posterPath,
                                backdropPath = it.backdropPath,
                                voteAverage = it.voteAverage ?: 0.0,
                                voteCount = it.voteCount ?: 0,
                                releaseDate = it.firstAirDate,
                                genreIds = it.genreIds ?: emptyList(),
                                mediaType = "tv"
                            )
                        }
                        val combined = mutableListOf<Movie>()
                        val maxSize = maxOf(movies.size, tvs.size)
                        for (i in 0 until maxSize) {
                            if (i < movies.size) combined.add(movies[i])
                            if (i < tvs.size) combined.add(tvs[i])
                        }
                        combined
                    }
                }
            } else {
                when (mediaType) {
                    "movie" -> {
                        val response = api.searchMovies(query, page)
                        response.results.map { it.toDomain() }
                    }
                    "tv" -> {
                        val response = api.searchTvShows(query, page)
                        response.results.map {
                            Movie(
                                id = it.id,
                                title = it.name ?: "",
                                overview = it.overview ?: "",
                                posterPath = it.posterPath,
                                backdropPath = it.backdropPath,
                                voteAverage = it.voteAverage ?: 0.0,
                                voteCount = it.voteCount ?: 0,
                                releaseDate = it.firstAirDate,
                                genreIds = it.genreIds ?: emptyList(),
                                mediaType = "tv"
                            )
                        }
                    }
                    else -> {
                        val response = api.searchMulti(query, page)
                        response.results
                            .filter { it.mediaType == "movie" || it.mediaType == "tv" }
                            .map { it.toMovie() }
                    }
                }
            }

            LoadResult.Page(
                data = results,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (results.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
