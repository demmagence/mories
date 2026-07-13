package com.demmagence.mories.ui.screens.genre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.demmagence.mories.ui.components.ErrorState
import com.demmagence.mories.ui.components.MovieCard
import com.demmagence.mories.ui.theme.MoriesBackground
import com.demmagence.mories.ui.theme.MoriesPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreListScreen(
    onBackClick: () -> Unit,
    onItemClick: (Int, String) -> Unit,
    viewModel: GenreListViewModel = hiltViewModel()
) {
    val items = viewModel.items.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = viewModel.genreName,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MoriesBackground,
                titleContentColor = Color.White
            ),
            windowInsets = WindowInsets(0.dp)
        )

        when (val loadState = items.loadState.refresh) {
            is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MoriesPrimary)
                }
            }
            is LoadState.Error -> {
                ErrorState(
                    message = loadState.error.localizedMessage ?: "Failed to load",
                    onRetry = { items.retry() }
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items.itemCount) { index ->
                        val item = items[index]
                        if (item != null) {
                            MovieCard(
                                posterPath = item.posterPath,
                                title = item.title,
                                voteAverage = item.voteAverage,
                                onClick = { onItemClick(item.id, item.mediaType) },
                                modifier = Modifier.fillMaxWidth().aspectRatio(2/3f)
                            )
                        }
                    }

                    if (items.loadState.append is LoadState.Loading) {
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
