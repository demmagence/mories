package com.demmagence.mories.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WatchlistEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MoriesDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
}
