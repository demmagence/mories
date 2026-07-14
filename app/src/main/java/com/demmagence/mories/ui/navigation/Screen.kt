package com.demmagence.mories.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Home : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Watchlist : Screen

    @Serializable
    data class MovieDetail(val movieId: Int) : Screen

    @Serializable
    data class TvDetail(val tvId: Int) : Screen

    @Serializable
    data class Player(
        val mediaType: String,
        val tmdbId: Int,
        val season: Int = 0,
        val episode: Int = 0,
        val title: String = ""
    ) : Screen

    @Serializable
    data class GenreList(
        val genreId: Int,
        val genreName: String,
        val mediaType: String
    ) : Screen
}
