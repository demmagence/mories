package com.demmagence.mories.data.repository

import com.demmagence.mories.data.remote.TmdbApiService
import com.demmagence.mories.data.remote.dto.toDomain
import com.demmagence.mories.domain.model.Genre
import com.demmagence.mories.domain.model.Movie
import com.demmagence.mories.domain.model.MovieDetail
import com.demmagence.mories.domain.repository.MovieRepository
import com.demmagence.mories.util.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApiService
) : MovieRepository {

    override suspend fun getTrendingMovies(): Resource<List<Movie>> {
        return try {
            val response = api.getTrendingMovies()
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getPopularMovies(page: Int): Resource<List<Movie>> {
        return try {
            val response = api.getPopularMovies(page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getTopRatedMovies(page: Int): Resource<List<Movie>> {
        return try {
            val response = api.getTopRatedMovies(page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getUpcomingMovies(page: Int): Resource<List<Movie>> {
        return try {
            val response = api.getUpcomingMovies(page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getNowPlayingMovies(page: Int): Resource<List<Movie>> {
        return try {
            val response = api.getNowPlayingMovies(page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getMovieDetails(movieId: Int): Resource<MovieDetail> {
        return try {
            val response = api.getMovieDetails(movieId)
            Resource.Success(response.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun searchMovies(query: String, page: Int): Resource<List<Movie>> {
        return try {
            val response = api.searchMovies(query, page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun getMovieGenres(): Resource<List<Genre>> {
        return try {
            val response = api.getMovieGenres()
            Resource.Success(response.genres.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }

    override suspend fun discoverMovies(genreId: Int, page: Int): Resource<List<Movie>> {
        return try {
            val response = api.discoverMovies(genreId, page)
            Resource.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An error occurred")
        }
    }
}
