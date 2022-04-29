package com.savvasdalkitsis.uhuruphotos.search.view.state

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

data class SearchState(
    val query: String = "",
    val showClearButton: Boolean = false,
    val searchResults: SearchResults = SearchResults.Idle,
    val userInformationState: UserInformationState = UserInformationState(),
    val showAccountOverview: Boolean = false,
    val showLogOutConfirmation: Boolean = false,
    val feedDisplay: FeedDisplay = FeedDisplay.default,
    val searchDisplay: FeedDisplay = FeedDisplay.default,
    val suggestion: String? = null,
)