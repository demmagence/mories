@file:Suppress("SetJavaScriptEnabled")

package com.demmagence.mories.ui.screens.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.demmagence.mories.ui.theme.MoriesPrimary
import com.demmagence.mories.ui.theme.MoriesOnSurfaceVariant
import com.demmagence.mories.ui.theme.MoriesTextSecondary
import kotlinx.coroutines.delay

/**
 * VidKing embed API parameters:
 * - color       : Hex color for player UI (without #), e.g. "e50914"
 * - autoPlay    : Start playing automatically (true/false)
 * - nextEpisode : Show next episode button, TV only (true/false)
 * - episodeSelector : Enable episode selection menu, TV only (true/false)
 *
 * Movie URL: https://www.vidking.net/embed/movie/{tmdbId}?color=e50914&autoPlay=true
 * TV URL:    https://www.vidking.net/embed/tv/{tmdbId}?s={season}&e={episode}&color=e50914&autoPlay=true&nextEpisode=true&episodeSelector=true
 */
private object VidKingApi {
    private const val BASE_URL = "https://www.vidking.net/embed"
    private const val COLOR = "e50914"

    fun getMovieUrl(tmdbId: Int): String =
        "$BASE_URL/movie/$tmdbId?color=$COLOR&autoPlay=true"

    fun getTvUrl(tmdbId: Int, season: Int, episode: Int): String =
        "$BASE_URL/tv/$tmdbId?s=$season&e=$episode&color=$COLOR&autoPlay=true&nextEpisode=true&episodeSelector=true"
}

/** Player states */
private enum class PlayerState {
    LOADING,
    PLAYING,
    ERROR
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PlayerScreen(
    mediaType: String,
    tmdbId: Int,
    season: Int = 0,
    episode: Int = 0,
    title: String = "",
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var playerState by remember { mutableStateOf(PlayerState.LOADING) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val embedUrl = remember {
        when (mediaType) {
            "movie" -> VidKingApi.getMovieUrl(tmdbId)
            "tv" -> VidKingApi.getTvUrl(tmdbId, season, episode)
            else -> VidKingApi.getMovieUrl(tmdbId)
        }
    }

    // Lock to landscape + immersive mode
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Enter immersive mode — hide status bar + navigation bar
        activity?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            // Restore system bars
            activity?.window?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false)
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Timeout: if loading takes > 30 seconds, show error
    LaunchedEffect(playerState) {
        if (playerState == PlayerState.LOADING) {
            delay(30_000L)
            if (playerState == PlayerState.LOADING) {
                playerState = PlayerState.ERROR
            }
        }
    }

    BackHandler { onBackClick() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // WebView layer
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewInstance = this

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Enable cookies for player sessions
                    val webView = this
                    CookieManager.getInstance().apply {
                        setAcceptCookie(true)
                        setAcceptThirdPartyCookies(webView, true)
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        allowFileAccess = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(false)
                        builtInZoomControls = false
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        javaScriptCanOpenWindowsAutomatically = false
                        setSupportMultipleWindows(false)
                        allowContentAccess = true
                        userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Mobile Safari/537.36"
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Small delay to let VidKing player initialize before showing
                            view?.postDelayed({
                                playerState = PlayerState.PLAYING
                            }, 1000)
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            // Only handle main frame errors
                            if (request?.isForMainFrame == true) {
                                playerState = PlayerState.ERROR
                            }
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString() ?: return false
                            // Allow VidKing domains and common video CDNs
                            val allowedDomains = listOf(
                                "vidking.net",
                                "googleapis.com",
                                "gstatic.com",
                                "cloudflare.com",
                                "jwpcdn.com",
                                "jwplayer.com",
                                "jwpsrv.com",
                                "cdn.jsdelivr.net",
                                "unpkg.com",
                                "plyr.io",
                                "hlsjs.video-dev.org",
                                "cdnjs.cloudflare.com",
                                "videasy.to"
                            )
                            return !allowedDomains.any { domain ->
                                url.contains(domain, ignoreCase = true)
                            }
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        // Block popup windows
                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: android.os.Message?
                        ): Boolean = false
                    }

                    loadUrl(embedUrl)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Loading overlay
        AnimatedVisibility(
            visible = playerState == PlayerState.LOADING,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(500))
        ) {
            LoadingOverlay(title = title)
        }

        // Error overlay
        AnimatedVisibility(
            visible = playerState == PlayerState.ERROR,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            ErrorOverlay(
                onRetry = {
                    playerState = PlayerState.LOADING
                    webViewInstance?.loadUrl(embedUrl)
                },
                onBack = onBackClick
            )
        }
    }
}

/**
 * Loading overlay with Mories branding.
 */
@Composable
private fun LoadingOverlay(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MoriesPrimary,
                modifier = Modifier.size(48.dp)
            )

            if (title.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}

/**
 * Error overlay with retry option.
 */
@Composable
private fun ErrorOverlay(
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Warning icon
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MoriesPrimary,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gagal memuat video",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Periksa koneksi internet Anda\ndan coba lagi",
                color = MoriesOnSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Retry button
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoriesPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Coba Lagi")
            }
        }
    }
}
