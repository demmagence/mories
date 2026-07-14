package com.demmagence.mories.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demmagence.mories.ui.components.ErrorState
import com.demmagence.mories.ui.components.HeroBanner
import com.demmagence.mories.ui.components.HeroBannerItem
import com.demmagence.mories.ui.components.MovieRow
import com.demmagence.mories.ui.components.MovieRowItem
import com.demmagence.mories.ui.components.ShimmerHeroBanner
import com.demmagence.mories.ui.components.ShimmerMovieRow
import com.demmagence.mories.ui.theme.MoriesBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    onTvClick: (Int) -> Unit,
    onPlayClick: (Int, String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.error != null && uiState.trendingMovies.isEmpty()) {
        ErrorState(
            message = uiState.error ?: "Unknown error",
            onRetry = { viewModel.loadHomeData() }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(MoriesBackground)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mories",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
            // Hero Banner
            if (uiState.isLoading) {
                ShimmerHeroBanner()
            } else {
                val heroBannerItems = buildList {
                    addAll(uiState.trendingMovies.take(3).map { movie ->
                        HeroBannerItem(
                            id = movie.id,
                            title = movie.title,
                            overview = movie.overview,
                            backdropPath = movie.backdropPath,
                            mediaType = "movie"
                        )
                    })
                    addAll(uiState.trendingTvShows.take(2).map { tv ->
                        HeroBannerItem(
                            id = tv.id,
                            title = tv.name,
                            overview = tv.overview,
                            backdropPath = tv.backdropPath,
                            mediaType = "tv"
                        )
                    })
                }

                HeroBanner(
                    items = heroBannerItems,
                    onItemClick = { id, mediaType ->
                        if (mediaType == "movie") onMovieClick(id) else onTvClick(id)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                repeat(4) {
                    ShimmerMovieRow()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                // Trending Movies
                MovieRow(
                    title = "Trending Movies",
                    items = uiState.trendingMovies.map { it.toRowItem() },
                    icon = Icons.Filled.Whatshot,
                    onItemClick = { id, _ -> onMovieClick(id) }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Trending TV Shows
                MovieRow(
                    title = "Trending Series",
                    items = uiState.trendingTvShows.map { it.toRowItem() },
                    icon = Icons.Filled.Whatshot,
                    onItemClick = { id, _ -> onTvClick(id) }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Now Playing
                MovieRow(
                    title = "Now Playing",
                    items = uiState.nowPlayingMovies.map { it.toRowItem() },
                    icon = Icons.Filled.Movie,
                    onItemClick = { id, _ -> onMovieClick(id) }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Popular Movies
                MovieRow(
                    title = "Popular Movies",
                    items = uiState.popularMovies.map { it.toRowItem() },
                    icon = Icons.Filled.Star,
                    onItemClick = { id, _ -> onMovieClick(id) }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Top Rated Movies
                MovieRow(
                    title = "Top Rated Movies",
                    items = uiState.topRatedMovies.map { it.toRowItem() },
                    icon = Icons.Filled.EmojiEvents,
                    onItemClick = { id, _ -> onMovieClick(id) }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Upcoming Movies
                MovieRow(
                    title = "Upcoming Movies",
                    items = uiState.upcomingMovies.map { it.toRowItem() },
                    icon = Icons.Filled.CalendarMonth,
                    onItemClick = { id, _ -> onMovieClick(id) }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Popular TV Shows
                MovieRow(
                    title = "Popular Series",
                    items = uiState.popularTvShows.map { it.toRowItem() },
                    icon = Icons.Filled.Star,
                    onItemClick = { id, _ -> onTvClick(id) }
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Top Rated TV Shows
                MovieRow(
                    title = "Top Rated Series",
                    items = uiState.topRatedTvShows.map { it.toRowItem() },
                    icon = Icons.Filled.EmojiEvents,
                    onItemClick = { id, _ -> onTvClick(id) }
                )
                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}
}

private fun com.demmagence.mories.domain.model.Movie.toRowItem() = MovieRowItem(
    id = id,
    title = title,
    posterPath = posterPath,
    voteAverage = voteAverage,
    mediaType = "movie"
)

private fun com.demmagence.mories.domain.model.TvShow.toRowItem() = MovieRowItem(
    id = id,
    title = name,
    posterPath = posterPath,
    voteAverage = voteAverage,
    mediaType = "tv"
)
