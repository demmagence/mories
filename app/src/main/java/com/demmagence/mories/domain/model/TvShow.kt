package com.demmagence.mories.domain.model

data class TvShow(
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val firstAirDate: String?,
    val genreIds: List<Int> = emptyList(),
    val popularity: Double = 0.0,
    val originalLanguage: String = "",
    val mediaType: String = "tv"
)
