package com.savvasdalkitsis.librephotos.search.service.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResults(
    val results: List<SearchResult>
)
