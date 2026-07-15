@file:Suppress("SetJavaScriptEnabled")

package com.demmagence.mories.ui.screens.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * VidSrc embed API.
 *
 * Movie URL: https://vidsrc.to/embed/movie/{tmdbId}
 * TV URL:    https://vidsrc.to/embed/tv/{tmdbId}/{season}/{episode}
 */
private object VidSrcApi {
    private const val BASE_URL = "https://vidsrc.to/embed"

    fun getMovieUrl(tmdbId: Int): String =
        "$BASE_URL/movie/$tmdbId"

    fun getTvUrl(tmdbId: Int, season: Int, episode: Int): String =
        "$BASE_URL/tv/$tmdbId/$season/$episode"
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

    val embedUrl = remember {
        when (mediaType) {
            "movie" -> VidSrcApi.getMovieUrl(tmdbId)
            "tv" -> VidSrcApi.getTvUrl(tmdbId, season, episode)
            else -> VidSrcApi.getMovieUrl(tmdbId)
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
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            // Let WebView handle loading internally
                            return false
                        }
                    }

                    loadUrl(embedUrl)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
