package com.demmagence.mories.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.demmagence.mories.ui.theme.MoriesStarYellow
import com.demmagence.mories.ui.theme.MoriesTextSecondary
import com.demmagence.mories.ui.theme.MoriesOnSurfaceVariant
import com.demmagence.mories.util.formatRating

@Composable
fun RatingBar(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 16.dp,
    showText: Boolean = true
) {
    val normalizedRating = (rating / 2).coerceIn(0.0, 5.0)
    val fullStars = normalizedRating.toInt()
    val hasHalfStar = (normalizedRating - fullStars) >= 0.5
    val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MoriesStarYellow,
                modifier = Modifier.size(starSize)
            )
        }
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Filled.StarHalf,
                contentDescription = null,
                tint = MoriesStarYellow,
                modifier = Modifier.size(starSize)
            )
        }
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Filled.StarOutline,
                contentDescription = null,
                tint = MoriesTextSecondary,
                modifier = Modifier.size(starSize)
            )
        }

        if (showText) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = rating.formatRating(),
                style = MaterialTheme.typography.bodySmall,
                color = MoriesOnSurfaceVariant
            )
        }
    }
}
