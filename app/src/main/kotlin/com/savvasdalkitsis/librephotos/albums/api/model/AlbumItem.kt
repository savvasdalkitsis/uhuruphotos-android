package com.savvasdalkitsis.librephotos.albums.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AlbumItem(
    val id: String,
    val dominantColor: String,
    val url: String,
    val location: String,
    val date: String,
    val birthTime: String,
    val aspectRatio: Float,
    val type: String,
    @Json(name = "video_length") val videoLength: String,
    val rating: Int
)