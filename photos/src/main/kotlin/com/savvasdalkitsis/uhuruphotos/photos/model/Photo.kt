package com.savvasdalkitsis.uhuruphotos.photos.model

data class Photo(
    val id: String,
    val thumbnailUrl: String? = null,
    val fullResUrl: String? = null,
    val fallbackColor: String? = null,
    val isFavourite: Boolean = false,
    val ratio: Float = 1f,
    val isVideo: Boolean = false,
    val isSelected: Boolean = false,
)
