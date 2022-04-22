package com.savvasdalkitsis.librephotos.photos.service.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoOperationResponse(
    @field:Json(name = "status") val status: Boolean,
    @field:Json(name = "results") val results: List<PhotoResult>,
)
