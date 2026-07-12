package com.demmagence.mories.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.demmagence.mories.ui.screens.detail.MovieDetailScreen
import com.demmagence.mories.ui.screens.detail.TvDetailScreen
import com.demmagence.mories.ui.screens.genre.GenreListScreen
import com.demmagence.mories.ui.screens.home.HomeScreen
import com.demmagence.mories.ui.screens.movies.MoviesScreen
import com.demmagence.mories.ui.screens.player.PlayerScreen
import com.demmagence.mories.ui.screens.search.SearchScreen
import com.demmagence.mories.ui.screens.series.SeriesScreen
import com.demmagence.mories.ui.screens.watchlist.WatchlistScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        }
    ) {
        // Bottom nav destinations
        composable<Screen.Home> {
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail(movieId))
                },
                onTvClick = { tvId ->
                    navController.navigate(Screen.TvDetail(tvId))
                },
                onPlayClick = { id, mediaType ->
                    if (mediaType == "movie") {
                        navController.navigate(
                            Screen.Player(mediaType = "movie", tmdbId = id)
                        )
                    } else {
                        navController.navigate(Screen.TvDetail(id))
                    }
                }
            )
        }

        composable<Screen.Movies> {
            MoviesScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail(movieId))
                }
            )
        }

        composable<Screen.Series> {
            SeriesScreen(
                onTvClick = { tvId ->
                    navController.navigate(Screen.TvDetail(tvId))
                }
            )
        }

        composable<Screen.Search> {
            SearchScreen(
                onItemClick = { id, mediaType ->
                    if (mediaType == "movie") {
                        navController.navigate(Screen.MovieDetail(id))
                    } else {
                        navController.navigate(Screen.TvDetail(id))
                    }
                }
            )
        }

        composable<Screen.Watchlist> {
            WatchlistScreen(
                onItemClick = { id, mediaType ->
                    if (mediaType == "movie") {
                        navController.navigate(Screen.MovieDetail(id))
                    } else {
                        navController.navigate(Screen.TvDetail(id))
                    }
                }
            )
        }

        // Detail screens
        composable<Screen.MovieDetail> {
            MovieDetailScreen(
                onBackClick = { navController.popBackStack() },
                onPlayClick = { movieId ->
                    navController.navigate(
                        Screen.Player(mediaType = "movie", tmdbId = movieId)
                    )
                },
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail(movieId))
                }
            )
        }

        composable<Screen.TvDetail> {
            TvDetailScreen(
                onBackClick = { navController.popBackStack() },
                onEpisodePlay = { tvId, season, episode ->
                    navController.navigate(
                        Screen.Player(
                            mediaType = "tv",
                            tmdbId = tvId,
                            season = season,
                            episode = episode
                        )
                    )
                },
                onTvClick = { tvId ->
                    navController.navigate(Screen.TvDetail(tvId))
                }
            )
        }

        // Player screen
        composable<Screen.Player> { backStackEntry ->
            val player = backStackEntry.toRoute<Screen.Player>()
            PlayerScreen(
                mediaType = player.mediaType,
                tmdbId = player.tmdbId,
                season = player.season,
                episode = player.episode,
                title = player.title,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Genre list screen
        composable<Screen.GenreList> {
            val genreList = it.toRoute<Screen.GenreList>()
            GenreListScreen(
                onBackClick = { navController.popBackStack() },
                onItemClick = { id, mediaType ->
                    if (mediaType == "movie") {
                        navController.navigate(Screen.MovieDetail(id))
                    } else {
                        navController.navigate(Screen.TvDetail(id))
                    }
                }
            )
        }
    }
}
