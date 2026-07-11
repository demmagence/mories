package com.demmagence.mories.data.local

import androidx.room.Entity

@Entity(
    tableName = "watchlist",
    primaryKeys = ["id", "mediaType"]
)
data class WatchlistEntity(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val mediaType: String,
    val voteAverage: Double,
    val addedAt: Long = System.currentTimeMillis()
)
