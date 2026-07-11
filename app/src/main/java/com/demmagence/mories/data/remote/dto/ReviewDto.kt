package com.demmagence.mories.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewListResponse(
    val page: Int? = null,
    val results: List<ReviewDto>? = null,
    @SerialName("total_pages") val totalPages: Int? = null,
    @SerialName("total_results") val totalResults: Int? = null
)

@Serializable
data class ReviewDto(
    val id: String,
    val author: String? = null,
    val content: String? = null,
    @SerialName("author_details") val authorDetails: AuthorDetailsDto? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class AuthorDetailsDto(
    val name: String? = null,
    val username: String? = null,
    val rating: Double? = null,
    @SerialName("avatar_path") val avatarPath: String? = null
)
