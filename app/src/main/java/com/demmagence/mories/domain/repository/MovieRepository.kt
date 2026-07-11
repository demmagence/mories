package com.demmagence.mories.domain.repository

import com.demmagence.mories.domain.model.Genre
import com.demmagence.mories.domain.model.Movie
import com.demmagence.mories.domain.model.MovieDetail
import com.demmagence.mories.util.Resource

interface MovieRepository {
    suspend fun getTrendingMovies(): Resource<List<Movie>>
    suspend fun getPopularMovies(page: Int): Resource<List<Movie>>
    suspend fun getTopRatedMovies(page: Int): Resource<List<Movie>>
    suspend fun getUpcomingMovies(page: Int): Resource<List<Movie>>
    suspend fun getNowPlayingMovies(page: Int): Resource<List<Movie>>
    suspend fun getMovieDetails(movieId: Int): Resource<MovieDetail>
    suspend fun searchMovies(query: String, page: Int): Resource<List<Movie>>
    suspend fun getMovieGenres(): Resource<List<Genre>>
    suspend fun discoverMovies(genreId: Int, page: Int): Resource<List<Movie>>
}
