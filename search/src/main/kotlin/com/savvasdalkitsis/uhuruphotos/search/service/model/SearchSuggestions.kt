package com.savvasdalkitsis.uhuruphotos.search.service.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchSuggestions(
    val results: List<String>
)
