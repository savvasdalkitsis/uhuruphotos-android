package com.savvasdalkitsis.librephotos.search.view.state

data class SearchState(
    val query: String = "",
    val showClearButton: Boolean = false,
    val searchResults: SearchResults = SearchResults.Idle,
)