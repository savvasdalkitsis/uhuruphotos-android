package com.savvasdalkitsis.uhuruphotos.search.view.state

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

data class SearchState(
    val query: String = "",
    val showClearButton: Boolean = false,
    val searchResults: SearchResults = SearchResults.Idle,
    val userInformationState: UserInformationState = UserInformationState(),
    val showAccountOverview: Boolean = false,
    val showLogOutConfirmation: Boolean = false,
    val feedDisplay: FeedDisplay = FeedDisplays.default,
    val searchDisplay: FeedDisplay = FeedDisplays.default,
    val suggestion: String? = null,
    val people: List<Person> = emptyList()
)