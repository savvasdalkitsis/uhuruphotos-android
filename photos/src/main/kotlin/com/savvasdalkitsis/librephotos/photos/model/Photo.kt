package com.savvasdalkitsis.librephotos.photos.model

data class Photo(
    val id: String,
    val url: String? = null,
    val fallbackColor: String? = null,
    val isFavourite: Boolean = false,
    val ratio: Float = 1f,
    val isVideo: Boolean = false,
    val isSelected: Boolean = false,
)
