package com.demmagence.mories.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.demmagence.mories.ui.theme.MoriesPrimary
import com.demmagence.mories.ui.theme.MoriesOnSurfaceVariant

data class MovieRowItem(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val voteAverage: Double,
    val mediaType: String = "movie"
)

@Composable
fun MovieRow(
    title: String,
    items: List<MovieRowItem>,
    onItemClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onSeeAllClick: (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MoriesPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (onSeeAllClick != null) {
                Text(
                    text = "See All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MoriesPrimary,
                    modifier = Modifier.clickable { onSeeAllClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (items.isEmpty()) {
            ShimmerMovieRow()
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items, key = { "${it.id}_${it.mediaType}" }) { item ->
                    MovieCard(
                        posterPath = item.posterPath,
                        title = item.title,
                        voteAverage = item.voteAverage,
                        onClick = { onItemClick(item.id, item.mediaType) }
                    )
                }
            }
        }
    }
}
