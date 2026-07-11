package com.demmagence.mories.util

object Constants {
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val TMDB_BACKDROP_SIZE = "w1280"
    const val TMDB_POSTER_SIZE = "w500"
    const val TMDB_PROFILE_SIZE = "w185"
    const val TMDB_STILL_SIZE = "w300"
    const val TMDB_ORIGINAL_SIZE = "original"

    const val VIDKING_BASE_URL = "https://www.vidking.net/"
    const val VIDKING_MOVIE_EMBED = "https://www.vidking.net/embed/movie/"
    const val VIDKING_TV_EMBED = "https://www.vidking.net/embed/tv/"

    const val YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v="
    const val YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/"

    const val ITEMS_PER_PAGE = 20

    fun getBackdropUrl(path: String?): String {
        return if (path != null) "${TMDB_IMAGE_BASE_URL}${TMDB_BACKDROP_SIZE}${path}" else ""
    }

    fun getPosterUrl(path: String?): String {
        return if (path != null) "${TMDB_IMAGE_BASE_URL}${TMDB_POSTER_SIZE}${path}" else ""
    }

    fun getProfileUrl(path: String?): String {
        return if (path != null) "${TMDB_IMAGE_BASE_URL}${TMDB_PROFILE_SIZE}${path}" else ""
    }

    fun getStillUrl(path: String?): String {
        return if (path != null) "${TMDB_IMAGE_BASE_URL}${TMDB_STILL_SIZE}${path}" else ""
    }

    fun getOriginalUrl(path: String?): String {
        return if (path != null) "${TMDB_IMAGE_BASE_URL}${TMDB_ORIGINAL_SIZE}${path}" else ""
    }

    fun getMovieEmbedUrl(tmdbId: Int): String {
        return "${VIDKING_MOVIE_EMBED}${tmdbId}"
    }

    fun getTvEmbedUrl(tmdbId: Int, season: Int, episode: Int): String {
        return "${VIDKING_TV_EMBED}${tmdbId}?s=${season}&e=${episode}"
    }
}
