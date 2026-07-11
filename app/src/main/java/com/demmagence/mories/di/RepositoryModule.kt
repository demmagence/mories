package com.demmagence.mories.di

import com.demmagence.mories.data.repository.MovieRepositoryImpl
import com.demmagence.mories.data.repository.TvRepositoryImpl
import com.demmagence.mories.data.repository.WatchlistRepositoryImpl
import com.demmagence.mories.domain.repository.MovieRepository
import com.demmagence.mories.domain.repository.TvRepository
import com.demmagence.mories.domain.repository.WatchlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    @Binds
    @Singleton
    abstract fun bindTvRepository(impl: TvRepositoryImpl): TvRepository

    @Binds
    @Singleton
    abstract fun bindWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository
}
