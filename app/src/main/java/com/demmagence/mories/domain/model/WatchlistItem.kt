package com.demmagence.mories.domain.model

data class WatchlistItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val mediaType: String,
    val voteAverage: Double,
    val addedAt: Long
)
