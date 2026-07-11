package com.demmagence.mories.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreditsDto(
    val cast: List<CastDto>? = null,
    val crew: List<CrewDto>? = null
)

@Serializable
data class CastDto(
    val id: Int,
    val name: String? = null,
    val character: String? = null,
    @SerialName("profile_path") val profilePath: String? = null,
    val order: Int? = null
)

@Serializable
data class CrewDto(
    val id: Int,
    val name: String? = null,
    val job: String? = null,
    val department: String? = null,
    @SerialName("profile_path") val profilePath: String? = null
)
