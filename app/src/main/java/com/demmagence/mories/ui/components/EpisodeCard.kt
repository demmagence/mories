package com.demmagence.mories.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.demmagence.mories.ui.theme.MoriesCardSurface
import com.demmagence.mories.ui.theme.MoriesPrimary
import com.demmagence.mories.ui.theme.MoriesOnSurfaceVariant
import com.demmagence.mories.ui.theme.MoriesTextSecondary
import com.demmagence.mories.util.Constants
import com.demmagence.mories.util.formatRuntime

@Composable
fun EpisodeCard(
    episodeNumber: Int,
    name: String,
    overview: String?,
    stillPath: String?,
    runtime: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MoriesCardSurface)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Episode still image
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(75.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            AsyncImage(
                model = Constants.getStillUrl(stillPath),
                contentDescription = name,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            // Play icon overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "E$episodeNumber • $name",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (runtime != null && runtime > 0) {
                Text(
                    text = runtime.formatRuntime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MoriesTextSecondary
                )
            }

            if (!overview.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MoriesOnSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
