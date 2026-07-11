package com.demmagence.mories.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String?,
    val genreIds: List<Int> = emptyList(),
    val popularity: Double = 0.0,
    val adult: Boolean = false,
    val originalLanguage: String = "",
    val mediaType: String = "movie"
)
