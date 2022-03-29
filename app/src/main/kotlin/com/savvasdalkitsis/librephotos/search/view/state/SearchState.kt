package com.savvasdalkitsis.librephotos.search.view.state

import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

data class SearchState(
    val query: String = "",
    val showClearButton: Boolean = false,
    val searchResults: SearchResults = SearchResults.Idle,
    val userBadgeState: UserBadgeState? = null,
)