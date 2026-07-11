package com.demmagence.mories.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailDto(
    val id: Int,
    val title: String? = null,
    val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
    @SerialName("vote_count") val voteCount: Int? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    val tagline: String? = null,
    val status: String? = null,
    val genres: List<GenreDto>? = null,
    val popularity: Double? = null,
    val credits: CreditsDto? = null,
    val similar: MovieListResponse? = null,
    val reviews: ReviewListResponse? = null,
    val videos: VideoListResponse? = null
)
