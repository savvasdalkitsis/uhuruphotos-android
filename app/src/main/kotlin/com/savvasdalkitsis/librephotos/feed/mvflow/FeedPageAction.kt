package com.savvasdalkitsis.librephotos.feed.mvflow

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.librephotos.photos.model.Photo

sealed class FeedPageAction {
    data class SelectedPhoto(val photo: Photo, val center: Offset, val scale: Float) : FeedPageAction()
    data class ChangeDisplay(val display: FeedDisplay) : FeedPageAction()
    object LoadFeed : FeedPageAction()
    object UserBadgePressed : FeedPageAction()
    object DismissAccountOverview : FeedPageAction()
    object LogOut : FeedPageAction()
    object RefreshAlbums : FeedPageAction()
    object ShowFeedDisplayChoice : FeedPageAction()
    object HideFeedDisplayChoice : FeedPageAction()
}
