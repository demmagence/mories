package com.demmagence.mories.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val releaseDate: String?,
    val runtime: Int?,
    val tagline: String?,
    val status: String?,
    val genres: List<Genre>,
    val cast: List<Cast>,
    val crew: List<Crew>,
    val similar: List<Movie>,
    val reviews: List<Review>,
    val videos: List<Video>,
    val popularity: Double = 0.0
)
