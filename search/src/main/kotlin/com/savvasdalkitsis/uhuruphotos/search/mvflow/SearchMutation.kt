package com.savvasdalkitsis.uhuruphotos.search.mvflow

import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.db.people.People
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

sealed class SearchMutation {

    object SearchCleared : SearchMutation()
    object SearchStarted : SearchMutation()
    object SearchStopped : SearchMutation()
    object ShowAccountOverview : SearchMutation()
    object HideAccountOverview : SearchMutation()
    object ShowLogOutConfirmation : SearchMutation()
    object HideLogOutConfirmation : SearchMutation()
    object HideSuggestions : SearchMutation()
    data class ChangeFeedDisplay(val display: FeedDisplay) : SearchMutation()
    data class ChangeSearchDisplay(val display: FeedDisplay) : SearchMutation()
    data class QueryChanged(val query: String) : SearchMutation()
    data class FocusChanged(val focused: Boolean) : SearchMutation()
    data class SearchResultsUpdated(val albums: List<Album>) : SearchMutation() {
        override fun toString() = "Updating results with ${albums.size} albums"
    }
    data class UserBadgeStateChanged(val userInformationState: UserInformationState) : SearchMutation()
    data class ShowSearchSuggestion(val suggestion: String) : SearchMutation()
    data class ShowPeople(val people: List<People>) : SearchMutation()
}