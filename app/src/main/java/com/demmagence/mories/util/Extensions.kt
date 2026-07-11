package com.demmagence.mories.util

import java.text.SimpleDateFormat
import java.util.Locale

fun String?.formatDate(): String {
    if (this.isNullOrBlank()) return "N/A"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(this)
        date?.let { outputFormat.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun Int?.formatRuntime(): String {
    if (this == null || this == 0) return "N/A"
    val hours = this / 60
    val minutes = this % 60
    return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}

fun Double?.formatRating(): String {
    if (this == null) return "N/A"
    return String.format(Locale.US, "%.1f", this)
}

fun Int?.formatVoteCount(): String {
    if (this == null) return ""
    return when {
        this >= 1000000 -> String.format(Locale.US, "%.1fM", this / 1000000.0)
        this >= 1000 -> String.format(Locale.US, "%.1fK", this / 1000.0)
        else -> this.toString()
    }
}
