@file:Suppress("SetJavaScriptEnabled")

package com.demmagence.mories.ui.screens.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
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
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.demmagence.mories.ui.theme.MoriesBackground
import com.demmagence.mories.ui.theme.MoriesPrimary
import com.demmagence.mories.ui.theme.MoriesOnSurfaceVariant
import com.demmagence.mories.ui.theme.MoriesTextSecondary
import kotlinx.coroutines.delay

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
    var currentProviderIndex by remember { mutableIntStateOf(0) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var hasTimedOut by remember { mutableStateOf(false) }

    val providers = StreamingProvider.providerChain
    val currentProvider = providers.getOrNull(currentProviderIndex) ?: providers.first()

    val embedUrl = remember(currentProviderIndex) {
        when (mediaType) {
            "movie" -> currentProvider.getMovieUrl(tmdbId)
            "tv" -> currentProvider.getTvUrl(tmdbId, season, episode)
            else -> currentProvider.getMovieUrl(tmdbId)
        }
    }

    // Lock to landscape + immersive mode
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        // Enter immersive mode
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

    // Timeout fallback: if loading takes > 20 seconds, try next provider
    LaunchedEffect(currentProviderIndex, playerState) {
        if (playerState == PlayerState.LOADING) {
            hasTimedOut = false
            delay(20_000L)
            if (playerState == PlayerState.LOADING) {
                hasTimedOut = true
                // Try next provider automatically
                if (currentProviderIndex < providers.size - 1) {
                    currentProviderIndex++
                    playerState = PlayerState.LOADING
                } else {
                    playerState = PlayerState.ERROR
                }
            }
        }
    }

    BackHandler { onBackClick() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // WebView layer (always present but hidden during loading/error)
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
                        userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Mobile Safari/537.36"
                        allowContentAccess = true
                        @Suppress("DEPRECATION")
                        allowUniversalAccessFromFileURLs = false
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                            // Keep loading state
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Inject CSS/JS overrides
                            view?.evaluateJavascript(buildInjectionScript(), null)

                            // Delay slightly then show player
                            view?.postDelayed({
                                playerState = PlayerState.PLAYING
                            }, 800)
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            // Only handle main frame errors
                            if (request?.isForMainFrame == true) {
                                if (currentProviderIndex < providers.size - 1) {
                                    currentProviderIndex++
                                    playerState = PlayerState.LOADING
                                } else {
                                    playerState = PlayerState.ERROR
                                }
                            }
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString() ?: return false
                            // Block navigation away from provider domains
                            return !StreamingProvider.isUrlAllowed(url, currentProvider)
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
            update = { webView ->
                // When provider changes, load new URL
                val currentUrl = webView.url ?: ""
                if (!currentUrl.contains(currentProvider.name) || playerState == PlayerState.LOADING) {
                    if (currentUrl != embedUrl) {
                        webView.loadUrl(embedUrl)
                    }
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
            LoadingOverlay(title = title, providerName = currentProvider.displayName)
        }

        // Error overlay
        AnimatedVisibility(
            visible = playerState == PlayerState.ERROR,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            ErrorOverlay(
                currentProviderIndex = currentProviderIndex,
                totalProviders = providers.size,
                onRetry = {
                    playerState = PlayerState.LOADING
                    webViewInstance?.loadUrl(embedUrl)
                },
                onTryNextServer = {
                    if (currentProviderIndex < providers.size - 1) {
                        currentProviderIndex++
                    } else {
                        currentProviderIndex = 0
                    }
                    playerState = PlayerState.LOADING
                },
                onBack = onBackClick
            )
        }
    }
}

/**
 * Builds the JavaScript injection script for:
 * 1. Color override (blue → red #E50914)
 * 2. Ad/overlay removal
 * 3. Popup blocking
 * 4. Touch event fix for play button
 */
private fun buildInjectionScript(): String = """
    (function() {
        'use strict';
        
        // === 1. COLOR OVERRIDE ===
        var style = document.createElement('style');
        style.textContent = `
            /* Override CSS custom properties */
            :root, *, *::before, *::after {
                --primary-color: #E50914 !important;
                --accent-color: #E50914 !important;
                --primary: #E50914 !important;
                --accent: #E50914 !important;
                --theme-color: #E50914 !important;
                --main-color: #E50914 !important;
                --brand-color: #E50914 !important;
                --color-primary: #E50914 !important;
                --highlight: #E50914 !important;
            }
            
            /* JW Player overrides */
            .jw-progress,
            .jw-slider-time .jw-progress,
            .jw-slider-volume .jw-progress {
                background-color: #E50914 !important;
                background: #E50914 !important;
            }
            .jw-button-color,
            .jw-svg-icon {
                color: #E50914 !important;
                fill: #E50914 !important;
            }
            .jw-icon-rewind .jw-svg-icon,
            .jw-icon-next .jw-svg-icon,
            .jw-icon-playback .jw-svg-icon {
                color: #FFFFFF !important;
                fill: #FFFFFF !important;
            }
            .jw-knob {
                background-color: #E50914 !important;
            }
            
            /* Video.js overrides */
            .vjs-play-progress,
            .vjs-volume-level {
                background-color: #E50914 !important;
                background: #E50914 !important;
            }
            .vjs-big-play-button {
                background-color: #E50914 !important;
                border-color: #E50914 !important;
            }
            .vjs-slider-handle,
            .vjs-play-progress::before {
                background-color: #E50914 !important;
                color: #E50914 !important;
            }
            
            /* Plyr overrides */
            .plyr--full-ui input[type=range]::-webkit-slider-thumb {
                background: #E50914 !important;
            }
            .plyr--full-ui input[type=range]:active::-webkit-slider-thumb {
                background: #E50914 !important;
            }
            .plyr__control--overlaid {
                background: #E50914 !important;
            }
            .plyr--video .plyr__control:hover {
                background: #E50914 !important;
            }
            
            /* Generic overrides for progress bars and buttons */
            [class*="progress"]:not([class*="bg"]),
            [class*="Progress"]:not([class*="bg"]) {
                background-color: #E50914 !important;
            }
            [class*="slider"] [class*="fill"],
            [class*="Slider"] [class*="Fill"],
            [class*="played"] {
                background-color: #E50914 !important;
            }
            [class*="knob"],
            [class*="Knob"],
            [class*="thumb"],
            [class*="Thumb"] {
                background-color: #E50914 !important;
            }
            
            /* Hide common ad/overlay elements */
            [id*="ad-"], [id*="ads-"], [id*="advert"],
            [class*="ad-overlay"], [class*="ads-overlay"],
            [class*="popup"], [class*="Popup"],
            [class*="overlay-ad"], [class*="ad_overlay"],
            [class*="banner-ad"], [class*="afs_ads"],
            div[data-ad], div[data-ads],
            iframe[src*="doubleclick"],
            iframe[src*="googlesyndication"],
            .ad-container, .ads-container,
            #player-ads, #video-ads,
            [class*="promo-"], [class*="sponsor"] {
                display: none !important;
                visibility: hidden !important;
                width: 0 !important;
                height: 0 !important;
                opacity: 0 !important;
                pointer-events: none !important;
            }
        `;
        document.head.appendChild(style);
        
        // === 2. INLINE COLOR OVERRIDE ===
        try {
            var blueShades = [
                'rgb(33, 150, 243)', 'rgb(25, 118, 210)',
                'rgb(30, 136, 229)', 'rgb(66, 165, 245)',
                'rgb(13, 71, 161)', 'rgb(21, 101, 192)',
                '#2196F3', '#1976D2', '#1E88E5',
                '#42A5F5', '#0D47A1', '#1565C0',
                '#2196f3', '#1976d2', '#1e88e5',
                '#42a5f5', '#0d47a1', '#1565c0'
            ];
            var elements = document.querySelectorAll('*');
            for (var i = 0; i < elements.length; i++) {
                var el = elements[i];
                var computed = window.getComputedStyle(el);
                if (blueShades.indexOf(computed.color) !== -1) {
                    el.style.setProperty('color', '#E50914', 'important');
                }
                if (blueShades.indexOf(computed.backgroundColor) !== -1) {
                    el.style.setProperty('background-color', '#E50914', 'important');
                }
                if (blueShades.indexOf(computed.borderColor) !== -1) {
                    el.style.setProperty('border-color', '#E50914', 'important');
                }
            }
        } catch(e) {}
        
        // === 3. BLOCK POPUPS ===
        window.open = function() { return null; };
        
        // Block alert/confirm/prompt
        window.alert = function() {};
        window.confirm = function() { return false; };
        window.prompt = function() { return null; };
        
        // === 4. FIX TOUCH EVENTS ===
        document.addEventListener('touchend', function(e) {
            var target = e.target;
            var playButton = target.closest('[class*="play"], [class*="Play"], button, [role="button"], .jw-icon-playback, .vjs-big-play-button, .plyr__control--overlaid');
            if (playButton) {
                e.preventDefault();
                e.stopPropagation();
                playButton.click();
                
                // Also try to find and play video element directly
                var video = document.querySelector('video');
                if (video && video.paused) {
                    try { video.play(); } catch(err) {}
                }
            }
        }, { passive: false, capture: true });
        
        // Re-run color override after a delay for dynamically loaded content
        setTimeout(function() {
            var style2 = document.createElement('style');
            style2.textContent = style.textContent;
            document.head.appendChild(style2);
        }, 3000);
        
        setTimeout(function() {
            var style3 = document.createElement('style');
            style3.textContent = style.textContent;
            document.head.appendChild(style3);
        }, 6000);
    })();
""".trimIndent()

/**
 * Premium loading overlay with Mories branding.
 */
@Composable
private fun LoadingOverlay(
    title: String,
    providerName: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

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
            // Pulsing play icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .alpha(pulseAlpha)
                    .background(MoriesPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Loading text
            Text(
                text = "Memuat...",
                color = MoriesOnSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Provider info
            Text(
                text = providerName,
                color = MoriesTextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Error overlay with retry and server switch options.
 */
@Composable
private fun ErrorOverlay(
    currentProviderIndex: Int,
    totalProviders: Int,
    onRetry: () -> Unit,
    onTryNextServer: () -> Unit,
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
                text = "Periksa koneksi internet Anda\natau coba server lain",
                color = MoriesOnSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Server ${currentProviderIndex + 1} dari $totalProviders",
                color = MoriesTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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

                // Try next server
                OutlinedButton(
                    onClick = onTryNextServer,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Filled.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Server Lain")
                }
            }
        }
    }
}
