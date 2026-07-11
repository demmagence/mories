package com.demmagence.mories.data.repository

import com.demmagence.mories.data.remote.TmdbApiService
import com.demmagence.mories.data.remote.dto.toDomain
import com.demmagence.mories.domain.model.Episode
import com.demmagence.mories.domain.model.Genre
import com.demmagence.mories.domain.model.TvShow
import com.demmagence.mories.domain.model.TvShowDetail
import com.demmagence.mories.domain.repository.TvRepository
import com.demmagence.mories.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvRepositoryImpl @Inject constructor(
    private val api: TmdbApiService
) : TvRepository {

    override suspend fun getTrendingTvShows(): Resource<List<TvShow>> {
        return try {
            val response = api.getTrendingTvShows()
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getPopularTvShows(page: Int): Resource<List<TvShow>> {
        return try {
            val response = api.getPopularTvShows(page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getTopRatedTvShows(page: Int): Resource<List<TvShow>> {
        return try {
            val response = api.getTopRatedTvShows(page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getTvShowDetails(tvId: Int): Resource<TvShowDetail> {
        return try {
            val response = api.getTvShowDetails(tvId)
            Resource.Success(response.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getTvSeasonEpisodes(tvId: Int, seasonNumber: Int): Resource<List<Episode>> {
        return try {
            val response = api.getTvSeasonDetails(tvId, seasonNumber)
            Resource.Success(response.episodes?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun searchTvShows(query: String, page: Int): Resource<List<TvShow>> {
        return try {
            val response = api.searchTvShows(query, page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getTvGenres(): Resource<List<Genre>> {
        return try {
            val response = api.getTvGenres()
            Resource.Success(response.genres.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun discoverTvShows(genreId: Int, page: Int): Resource<List<TvShow>> {
        return try {
            val response = api.discoverTvShows(genreId, page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }
}
