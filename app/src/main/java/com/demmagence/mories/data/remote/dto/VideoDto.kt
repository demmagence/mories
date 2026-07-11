package com.demmagence.mories.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoListResponse(
    val results: List<VideoDto>? = null
)

@Serializable
data class VideoDto(
    val id: String,
    val key: String? = null,
    val name: String? = null,
    val site: String? = null,
    val type: String? = null,
    val official: Boolean? = null
)
