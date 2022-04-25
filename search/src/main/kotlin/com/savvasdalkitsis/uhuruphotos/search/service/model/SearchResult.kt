package com.savvasdalkitsis.uhuruphotos.search.service.model

import com.savvasdalkitsis.uhuruphotos.photos.service.model.PhotoSummaryItem
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResult(
    val date: String,
    val location: String,
    val items: List<PhotoSummaryItem>
)