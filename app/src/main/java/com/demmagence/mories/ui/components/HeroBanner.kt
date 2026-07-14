package com.demmagence.mories.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.demmagence.mories.ui.theme.MoriesBackground
import com.demmagence.mories.ui.theme.MoriesPrimary
import com.demmagence.mories.util.Constants
import kotlinx.coroutines.delay

data class HeroBannerItem(
    val id: Int,
    val title: String,
    val overview: String,
    val backdropPath: String?,
    val mediaType: String = "movie"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroBanner(
    items: List<HeroBannerItem>,
    onItemClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) {
        ShimmerHeroBanner(modifier = modifier)
        return
    }

    val displayItems = items.take(5)
    val baseSize = displayItems.size
    val pageCount = 10000 * baseSize
    val startIndex = (pageCount / 2) - ((pageCount / 2) % baseSize)
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { pageCount }
    )

    // Auto-scroll
    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)
            val nextPage = if (pagerState.currentPage + 1 < pageCount) {
                pagerState.currentPage + 1
            } else {
                startIndex
            }
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = modifier.fillMaxWidth().height(440.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = displayItems[page % baseSize]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onItemClick(item.id, item.mediaType) }
            ) {
                // Backdrop image
                AsyncImage(
                    model = Constants.getOriginalUrl(item.backdropPath),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlays
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

                // Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
