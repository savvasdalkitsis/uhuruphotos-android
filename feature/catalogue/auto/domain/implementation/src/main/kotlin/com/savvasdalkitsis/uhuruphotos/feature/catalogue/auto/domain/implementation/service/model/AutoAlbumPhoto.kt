package com.savvasdalkitsis.uhuruphotos.feature.catalogue.auto.domain.implementation.service.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoAlbumPhoto(
    @field:Json(name = "image_hash")
    val imageHash: String,
    val video: Boolean,
)