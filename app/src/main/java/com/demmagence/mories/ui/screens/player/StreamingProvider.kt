package com.demmagence.mories.ui.screens.player

/**
 * Defines available streaming embed providers with fallback chain support.
 * Each provider uses TMDB IDs to generate embed URLs for movies and TV shows.
 */
sealed class StreamingProvider(
    val name: String,
    val displayName: String
) {
    abstract fun getMovieUrl(tmdbId: Int): String
    abstract fun getTvUrl(tmdbId: Int, season: Int, episode: Int): String

    /** Returns all allowed domains for this provider (used for URL filtering). */
    abstract val allowedDomains: List<String>

    data object VidKing : StreamingProvider(
        name = "vidking",
        displayName = "Server 1"
    ) {
        override fun getMovieUrl(tmdbId: Int): String =
            "https://www.vidking.net/embed/movie/$tmdbId"

        override fun getTvUrl(tmdbId: Int, season: Int, episode: Int): String =
            "https://www.vidking.net/embed/tv/$tmdbId?s=$season&e=$episode"

        override val allowedDomains = listOf(
            "vidking.net",
            "www.vidking.net"
        )
    }

    data object EmbedSu : StreamingProvider(
        name = "embedsu",
        displayName = "Server 2"
    ) {
        override fun getMovieUrl(tmdbId: Int): String =
            "https://embed.su/embed/movie/$tmdbId"

        override fun getTvUrl(tmdbId: Int, season: Int, episode: Int): String =
            "https://embed.su/embed/tv/$tmdbId/$season/$episode"

        override val allowedDomains = listOf(
            "embed.su",
            "www.embed.su"
        )
    }

    data object SuperEmbed : StreamingProvider(
        name = "superembed",
        displayName = "Server 3"
    ) {
        override fun getMovieUrl(tmdbId: Int): String =
            "https://multiembed.mov/?video_id=$tmdbId&tmdb=1"

        override fun getTvUrl(tmdbId: Int, season: Int, episode: Int): String =
            "https://multiembed.mov/?video_id=$tmdbId&tmdb=1&s=$season&e=$episode"

        override val allowedDomains = listOf(
            "multiembed.mov",
            "www.multiembed.mov"
        )
    }

    companion object {
        /** Ordered list of providers for fallback chain. */
        val providerChain: List<StreamingProvider> = listOf(
            VidKing,
            EmbedSu,
            SuperEmbed
        )

        /** Common CDN / video hosting domains that should always be allowed. */
        val commonAllowedDomains = listOf(
            "googleapis.com",
            "gstatic.com",
            "cloudflare.com",
            "cdn.jsdelivr.net",
            "unpkg.com",
            "jwpcdn.com",
            "jwplayer.com",
            "jwpsrv.com",
            "vjs.zencdn.net",
            "videojs.com",
            "plyr.io",
            "hlsjs.video-dev.org",
            "cdnjs.cloudflare.com"
        )

        /**
         * Checks if a URL should be allowed to load in the WebView.
         * Only allows the active provider's domains and common CDN domains.
         */
        fun isUrlAllowed(url: String, activeProvider: StreamingProvider): Boolean {
            val allAllowed = activeProvider.allowedDomains + commonAllowedDomains
            return allAllowed.any { domain ->
                url.contains(domain, ignoreCase = true)
            }
        }
    }
}
