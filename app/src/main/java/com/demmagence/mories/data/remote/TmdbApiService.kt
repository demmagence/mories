package com.demmagence.mories.data.remote

import com.demmagence.mories.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    // Trending
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): MovieListResponse

    @GET("trending/tv/week")
    suspend fun getTrendingTvShows(): TvListResponse

    // Movies
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") appendToResponse: String = "credits,similar,reviews,videos"
    ): MovieDetailDto

    // TV Shows
    @GET("tv/popular")
    suspend fun getPopularTvShows(@Query("page") page: Int = 1): TvListResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedTvShows(@Query("page") page: Int = 1): TvListResponse

    @GET("tv/{tv_id}")
    suspend fun getTvShowDetails(
        @Path("tv_id") tvId: Int,
        @Query("append_to_response") appendToResponse: String = "credits,similar,reviews,videos"
    ): TvDetailDto

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getTvSeasonDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int
    ): SeasonDetailDto

    // Search
    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): SearchMultiResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("search/tv")
    suspend fun searchTvShows(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TvListResponse

    // Genres
    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreListResponse

    @GET("genre/tv/list")
    suspend fun getTvGenres(): GenreListResponse

    // Discover
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): MovieListResponse

    @GET("discover/tv")
    suspend fun discoverTvShows(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = "popularity.desc"
    ): TvListResponse
}
