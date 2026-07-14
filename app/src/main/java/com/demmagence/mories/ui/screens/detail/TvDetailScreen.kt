package com.demmagence.mories.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.demmagence.mories.ui.components.CastCard
import com.demmagence.mories.ui.components.EpisodeCard
import com.demmagence.mories.ui.components.ErrorState
import com.demmagence.mories.ui.components.GenreChip
import com.demmagence.mories.ui.components.MovieCard
import com.demmagence.mories.ui.components.RatingBar
import com.demmagence.mories.ui.components.ShimmerDetailScreen
import com.demmagence.mories.ui.theme.MoriesBackground
import com.demmagence.mories.ui.theme.MoriesOnSurfaceVariant
import com.demmagence.mories.ui.theme.MoriesPrimary
import com.demmagence.mories.ui.theme.MoriesSuccess
import com.demmagence.mories.ui.theme.MoriesTextSecondary
import com.demmagence.mories.util.Constants
import com.demmagence.mories.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvDetailScreen(
    onBackClick: () -> Unit,
    onEpisodePlay: (Int, Int, Int) -> Unit, // tvId, season, episode
    onTvClick: (Int) -> Unit,
    viewModel: TvDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when {
        uiState.isLoading -> ShimmerDetailScreen()
        uiState.error != null -> ErrorState(
            message = uiState.error ?: "Unknown error",
            onRetry = { viewModel.retry() }
        )
        uiState.tvDetail != null -> {
            val detail = uiState.tvDetail!!
            var showSeasonDropdown by remember { mutableStateOf(false) }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MoriesBackground,
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MoriesBackground
                        ),
                        windowInsets = WindowInsets(0.dp)
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                    // Backdrop
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        AsyncImage(
                            model = Constants.getOriginalUrl(detail.backdropPath),
                            contentDescription = detail.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MoriesBackground,
                                            MoriesBackground.copy(alpha = 0.5f),
                                            Color.Transparent,
                                            Color.Transparent,
                                            MoriesBackground.copy(alpha = 0.8f),
                                            MoriesBackground
                                        ),
                                        startY = 0f
                                    )
                                )
                        )
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        // Title
                        Text(
                            text = detail.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        if (!detail.tagline.isNullOrBlank()) {
                            Text(
                                text = detail.tagline,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MoriesOnSurfaceVariant,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Info row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RatingBar(rating = detail.voteAverage)
                            Text(
                                text = detail.firstAirDate.formatDate(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MoriesTextSecondary
                            )
                            Text(
                                text = "${detail.numberOfSeasons} Season${if (detail.numberOfSeasons > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MoriesTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Genre chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            detail.genres.take(4).forEach { genre ->
                                GenreChip(name = genre.name)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Watchlist button
                        OutlinedButton(
                            onClick = { viewModel.toggleWatchlist() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                if (uiState.isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = null,
                                tint = if (uiState.isInWatchlist) MoriesSuccess else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (uiState.isInWatchlist) "Added to Watchlist" else "Add to Watchlist",
                                color = if (uiState.isInWatchlist) MoriesSuccess else Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Trailer button
                        val trailer = detail.videos.firstOrNull {
                            it.site == "YouTube" && it.type == "Trailer"
                        }
                        if (trailer != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_VIDEO_URL + trailer.key))
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Watch Trailer", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Overview
                        Text(
                            text = "Overview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = detail.overview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MoriesOnSurfaceVariant
                        )

                        // Cast
                        if (detail.cast.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Cast",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (detail.cast.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(detail.cast.take(20)) { castMember ->
                                CastCard(
                                    name = castMember.name,
                                    character = castMember.character,
                                    profilePath = castMember.profilePath
                                )
                            }
                        }
                    }

                    // Episodes section
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Spacer(modifier = Modifier.height(20.dp))

                        // Season selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Episodes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Box {
                                TextButton(onClick = { showSeasonDropdown = true }) {
                                    Text(
                                        text = "Season ${uiState.selectedSeason}",
                                        color = MoriesPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                DropdownMenu(
                                    expanded = showSeasonDropdown,
                                    onDismissRequest = { showSeasonDropdown = false }
                                ) {
                                    detail.seasons
                                        .filter { it.seasonNumber > 0 }
                                        .forEach { season ->
                                            DropdownMenuItem(
                                                text = { Text(season.name) },
                                                onClick = {
                                                    viewModel.selectSeason(season.seasonNumber)
                                                    showSeasonDropdown = false
                                                }
                                            )
                                        }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.isLoadingEpisodes) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MoriesPrimary)
                            }
                        } else {
                            uiState.episodes.forEach { episode ->
                                EpisodeCard(
                                    episodeNumber = episode.episodeNumber,
                                    name = episode.name,
                                    overview = episode.overview,
                                    stillPath = episode.stillPath,
                                    runtime = episode.runtime,
                                    onClick = {
                                        onEpisodePlay(detail.id, uiState.selectedSeason, episode.episodeNumber)
                                    }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    // Similar Shows
                    if (detail.similar.isNotEmpty()) {
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "More Like This",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(detail.similar) { tvShow ->
                                MovieCard(
                                    posterPath = tvShow.posterPath,
                                    title = tvShow.name,
                                    voteAverage = tvShow.voteAverage,
                                    onClick = { onTvClick(tvShow.id) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
}
