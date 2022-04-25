package com.savvasdalkitsis.uhuruphotos.feedpage.mvflow

import com.savvasdalkitsis.uhuruphotos.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

sealed class FeedPageMutation {
    object Loading : FeedPageMutation()
    object FinishedLoading : FeedPageMutation()
    object ShowAccountOverview : FeedPageMutation()
    object HideAccountOverview : FeedPageMutation()
    object ShowLogOutConfirmation : FeedPageMutation()
    object HideLogOutConfirmation : FeedPageMutation()
    object StartRefreshing : FeedPageMutation()
    object StopRefreshing : FeedPageMutation()
    object ShowFeedDisplayChoice : FeedPageMutation()
    object HideFeedDisplayChoice : FeedPageMutation()
    object ShowDeletionConfirmationDialog : FeedPageMutation()
    object HideDeletionConfirmationDialog : FeedPageMutation()

    data class ShowAlbums(val albums: List<Album>) : FeedPageMutation() {
        override fun toString() = "Showing ${albums.size} albums"
    }
    data class UserBadgeUpdate(val state: UserInformationState) : FeedPageMutation()
    data class ChangeDisplay(val display: FeedDisplay) : FeedPageMutation()
}
