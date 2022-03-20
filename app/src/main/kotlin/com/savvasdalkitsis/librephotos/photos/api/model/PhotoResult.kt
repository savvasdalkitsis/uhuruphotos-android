package com.savvasdalkitsis.librephotos.photos.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoResult(
    @field:Json(name = "square_thumbnail_url") val bigThumbnailUrl: String
)
