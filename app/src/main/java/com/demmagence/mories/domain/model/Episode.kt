package com.demmagence.mories.domain.model

data class Episode(
    val id: Int,
    val episodeNumber: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String?,
    val stillPath: String?,
    val airDate: String?,
    val runtime: Int?,
    val voteAverage: Double
)
