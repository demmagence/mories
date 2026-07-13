package com.demmagence.mories.ui.screens.series

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.demmagence.mories.ui.components.ErrorState
import com.demmagence.mories.ui.components.GenreChip
import com.demmagence.mories.ui.components.MovieCard
import com.demmagence.mories.ui.theme.MoriesBackground
import com.demmagence.mories.ui.theme.MoriesPrimary

@Composable
fun SeriesScreen(
    onTvClick: (Int) -> Unit,
    viewModel: SeriesViewModel = hiltViewModel()
) {
    val genres by viewModel.genres.collectAsStateWithLifecycle()
    val selectedGenreId by viewModel.selectedGenreId.collectAsStateWithLifecycle()
    val tvShows = viewModel.tvShows.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize().background(MoriesBackground)) {
        // Header
        Text(
            text = "TV Series",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Genre filter chips
        if (genres.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(genres) { genre ->
                    GenreChip(
                        name = genre.name,
                        isSelected = selectedGenreId == genre.id,
                        onClick = { viewModel.selectGenre(genre.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Series grid
        when (val loadState = tvShows.loadState.refresh) {
            is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MoriesPrimary)
                }
            }
            is LoadState.Error -> {
                ErrorState(
                    message = loadState.error.localizedMessage ?: "Failed to load series",
                    onRetry = { tvShows.retry() }
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tvShows.itemCount) { index ->
                        val tvShow = tvShows[index]
                        if (tvShow != null) {
                            MovieCard(
                                posterPath = tvShow.posterPath,
                                title = tvShow.name,
                                voteAverage = tvShow.voteAverage,
                                onClick = { onTvClick(tvShow.id) },
                                modifier = Modifier.fillMaxWidth().aspectRatio(2/3f)
                            )
                        }
                    }

                    if (tvShows.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MoriesPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
