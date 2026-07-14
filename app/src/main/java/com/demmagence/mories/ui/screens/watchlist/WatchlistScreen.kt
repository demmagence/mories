package com.demmagence.mories.ui.screens.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demmagence.mories.ui.components.MovieCard
import com.demmagence.mories.ui.theme.MoriesBackground
import com.demmagence.mories.ui.theme.MoriesTextSecondary

@Composable
fun WatchlistScreen(
    onItemClick: (Int, String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel()
) {
    val watchlistItems by viewModel.watchlistItems.collectAsStateWithLifecycle(initialValue = null)

    Column(modifier = Modifier.fillMaxSize().background(MoriesBackground)) {
        // Header
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

        if (watchlistItems == null) {
            // Show nothing (blank background) during initial load to prevent flicker
            Box(modifier = Modifier.fillMaxSize())
        } else if (watchlistItems!!.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MoriesTextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your watchlist is empty",
                        style = MaterialTheme.typography.titleMedium,
                        color = MoriesTextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add movies & series to watch later",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MoriesTextSecondary
                    )
                }
            }
        } else {
            val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = bottomPadding + 80.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(watchlistItems!!, key = { "${it.id}_${it.mediaType}" }) { item ->
                    MovieCard(
                        posterPath = item.posterPath,
                        title = item.title,
                        voteAverage = item.voteAverage,
                        onClick = { onItemClick(item.id, item.mediaType) },
                        modifier = Modifier.fillMaxWidth().aspectRatio(2/3f)
                    )
                }
            }
        }
    }
}
