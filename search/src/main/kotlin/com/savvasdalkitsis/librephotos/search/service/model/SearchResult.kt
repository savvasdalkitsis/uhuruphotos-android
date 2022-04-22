package com.savvasdalkitsis.librephotos.search.service.model

import com.savvasdalkitsis.librephotos.photos.service.model.PhotoSummaryItem
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    val date: String,
    val location: String,
    val items: List<PhotoSummaryItem>
)