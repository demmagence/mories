package com.demmagence.mories.domain.model

data class TvShowDetail(
    val id: Int,
    val name: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val firstAirDate: String?,
    val lastAirDate: String?,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val status: String?,
    val tagline: String?,
    val genres: List<Genre>,
    val seasons: List<Season>,
    val cast: List<Cast>,
    val crew: List<Crew>,
    val similar: List<TvShow>,
    val reviews: List<Review>,
    val videos: List<Video>,
    val popularity: Double = 0.0
)
