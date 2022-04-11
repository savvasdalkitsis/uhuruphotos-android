package com.savvasdalkitsis.librephotos.feed.mvflow

import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

sealed class FeedPageMutation {
    object Loading : FeedPageMutation()
    object FinishedLoading : FeedPageMutation()
    object ShowAccountOverview : FeedPageMutation()
    object HideAccountOverview : FeedPageMutation()
    object StartRefreshing : FeedPageMutation()
    object StopRefreshing : FeedPageMutation()
    object ShowFeedDisplayChoice : FeedPageMutation()
    object HideFeedDisplayChoice : FeedPageMutation()

    data class ShowAlbums(val albums: List<Album>) : FeedPageMutation() {
        override fun toString() = "Showing ${albums.size} albums"
    }
    data class UserBadgeUpdate(val state: UserBadgeState) : FeedPageMutation()
    data class ChangeDisplay(val display: FeedDisplay) : FeedPageMutation()
}
