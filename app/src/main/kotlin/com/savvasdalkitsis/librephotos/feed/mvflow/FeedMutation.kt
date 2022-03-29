package com.savvasdalkitsis.librephotos.feed.mvflow

import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

sealed class FeedMutation {
    object Loading : FeedMutation()
    object FinishedLoading : FeedMutation()
    data class ShowAlbums(val albums: List<Album>) : FeedMutation() {
        override fun toString() = "Showing ${albums.size} albums"
    }
    data class UserBadgeUpdate(val state: UserBadgeState) : FeedMutation()
}
