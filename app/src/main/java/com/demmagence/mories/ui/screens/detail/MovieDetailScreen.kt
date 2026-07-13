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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.demmagence.mories.util.formatRuntime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    onBackClick: () -> Unit,
    onPlayClick: (Int) -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when {
        uiState.isLoading -> ShimmerDetailScreen()
        uiState.error != null -> ErrorState(
            message = uiState.error ?: "Unknown error",
            onRetry = { viewModel.retry() }
        )
        uiState.movieDetail != null -> {
            val detail = uiState.movieDetail!!
            var showFullOverview by remember { mutableStateOf(false) }

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
                            contentDescription = detail.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MoriesBackground.copy(alpha = 0.3f),
                                            MoriesBackground
                                        )
                                    )
                                )
                        )
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        // Title
                        Text(
                            text = detail.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Tagline
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
                                text = detail.releaseDate.formatDate(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MoriesTextSecondary
                            )
                            Text(
                                text = detail.runtime.formatRuntime(),
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

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { onPlayClick(detail.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MoriesPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Play", fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.toggleWatchlist() },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    if (uiState.isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                                    contentDescription = null,
                                    tint = if (uiState.isInWatchlist) MoriesSuccess else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    if (uiState.isInWatchlist) "Added" else "Watchlist",
                                    color = if (uiState.isInWatchlist) MoriesSuccess else Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
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
                            color = MoriesOnSurfaceVariant,
                            maxLines = if (showFullOverview) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (detail.overview.length > 200) {
                            Text(
                                text = if (showFullOverview) "Show Less" else "Read More",
                                style = MaterialTheme.typography.labelLarge,
                                color = MoriesPrimary,
                                modifier = Modifier.padding(top = 4.dp).then(
                                    Modifier.background(Color.Transparent)
                                ).also {
                                    // Make clickable via modifier
                                },
                                fontWeight = FontWeight.SemiBold
                            )
                        }

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

                    // Reviews
                    if (detail.reviews.isNotEmpty()) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Reviews",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            detail.reviews.take(3).forEach { review ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Color.White.copy(alpha = 0.05f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = review.author,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        if (review.rating != null) {
                                            RatingBar(rating = review.rating, starSize = 12.dp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = review.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MoriesOnSurfaceVariant,
                                        maxLines = 5,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    // Similar Movies
                    if (detail.similar.isNotEmpty()) {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 0.dp)) {
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
                            items(detail.similar) { movie ->
                                MovieCard(
                                    posterPath = movie.posterPath,
                                    title = movie.title,
                                    voteAverage = movie.voteAverage,
                                    onClick = { onMovieClick(movie.id) }
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
