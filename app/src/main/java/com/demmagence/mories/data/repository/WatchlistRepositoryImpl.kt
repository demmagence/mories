package com.demmagence.mories.data.repository

import com.demmagence.mories.data.local.WatchlistDao
import com.demmagence.mories.data.local.WatchlistEntity
import com.demmagence.mories.domain.model.WatchlistItem
import com.demmagence.mories.domain.repository.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepositoryImpl @Inject constructor(
    private val watchlistDao: WatchlistDao
) : WatchlistRepository {

    override fun getAllWatchlistItems(): Flow<List<WatchlistItem>> {
        return watchlistDao.getAllWatchlistItems().map { entities ->
            entities.map { entity ->
                WatchlistItem(
                    id = entity.id,
                    title = entity.title,
                    posterPath = entity.posterPath,
                    mediaType = entity.mediaType,
                    voteAverage = entity.voteAverage,
                    addedAt = entity.addedAt
                )
            }
        }
    }

    override suspend fun addToWatchlist(item: WatchlistItem) {
        watchlistDao.insert(
            WatchlistEntity(
                id = item.id,
                title = item.title,
                posterPath = item.posterPath,
                mediaType = item.mediaType,
                voteAverage = item.voteAverage,
                addedAt = item.addedAt
            )
        )
    }

    override suspend fun removeFromWatchlist(id: Int, mediaType: String) {
        watchlistDao.delete(id, mediaType)
    }

    override suspend fun isInWatchlist(id: Int, mediaType: String): Boolean {
        return watchlistDao.isInWatchlist(id, mediaType)
    }
}
