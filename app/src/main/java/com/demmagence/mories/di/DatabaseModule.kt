package com.demmagence.mories.di

import android.content.Context
import androidx.room.Room
import com.demmagence.mories.data.local.MoriesDatabase
import com.demmagence.mories.data.local.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoriesDatabase {
        return Room.databaseBuilder(
            context,
            MoriesDatabase::class.java,
            "mories_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideWatchlistDao(database: MoriesDatabase): WatchlistDao {
        return database.watchlistDao()
    }
}
