package com.savvasdalkitsis.librephotos.search.mvflow

import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

sealed class SearchMutation {

    object SearchCleared : SearchMutation()
    object SearchStarted : SearchMutation()
    object ShowAccountOverview : SearchMutation()
    object HideAccountOverview : SearchMutation()

    data class QueryChanged(val query: String) : SearchMutation()
    data class FocusChanged(val focused: Boolean) : SearchMutation()
    data class SearchResultsUpdated(val albums: List<Album>) : SearchMutation() {
        override fun toString() = "Updating results with ${albums.size} albums"
    }
    data class UserBadgeStateChanged(val userBadgeState: UserBadgeState) : SearchMutation()
}