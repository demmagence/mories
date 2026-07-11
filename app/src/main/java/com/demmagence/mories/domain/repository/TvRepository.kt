package com.demmagence.mories.domain.repository

import com.demmagence.mories.domain.model.Episode
import com.demmagence.mories.domain.model.Genre
import com.demmagence.mories.domain.model.TvShow
import com.demmagence.mories.domain.model.TvShowDetail
import com.demmagence.mories.util.Resource

interface TvRepository {
    suspend fun getTrendingTvShows(): Resource<List<TvShow>>
    suspend fun getPopularTvShows(page: Int): Resource<List<TvShow>>
    suspend fun getTopRatedTvShows(page: Int): Resource<List<TvShow>>
    suspend fun getTvShowDetails(tvId: Int): Resource<TvShowDetail>
    suspend fun getTvSeasonEpisodes(tvId: Int, seasonNumber: Int): Resource<List<Episode>>
    suspend fun searchTvShows(query: String, page: Int): Resource<List<TvShow>>
    suspend fun getTvGenres(): Resource<List<Genre>>
    suspend fun discoverTvShows(genreId: Int, page: Int): Resource<List<TvShow>>
}
