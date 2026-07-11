package com.demmagence.mories.domain.repository

import com.demmagence.mories.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getAllWatchlistItems(): Flow<List<WatchlistItem>>
    suspend fun addToWatchlist(item: WatchlistItem)
    suspend fun removeFromWatchlist(id: Int, mediaType: String)
    suspend fun isInWatchlist(id: Int, mediaType: String): Boolean
}
