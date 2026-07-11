package com.demmagence.mories.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC")
    fun getAllWatchlistItems(): Flow<List<WatchlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE id = :id AND mediaType = :mediaType")
    suspend fun delete(id: Int, mediaType: String)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :id AND mediaType = :mediaType)")
    suspend fun isInWatchlist(id: Int, mediaType: String): Boolean
}
