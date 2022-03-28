package com.savvasdalkitsis.librephotos.search.api.model

import com.savvasdalkitsis.librephotos.photos.api.model.PhotoSummaryItem
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    val date: String,
    val location: String,
    val items: List<PhotoSummaryItem>
)