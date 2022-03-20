package com.savvasdalkitsis.librephotos.photos.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photos(
    val count: Int,
    val results: List<PhotoResult>,
)
