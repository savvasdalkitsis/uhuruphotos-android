package com.savvasdalkitsis.librephotos.feedpage.mvflow

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.librephotos.albums.model.Album
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.librephotos.photos.model.Photo

sealed class FeedPageAction {
    data class SelectedPhoto(val photo: Photo, val center: Offset, val scale: Float) : FeedPageAction()
    data class ChangeDisplay(val display: FeedDisplay) : FeedPageAction()
    data class PhotoLongPressed(val photo: Photo) : FeedPageAction()
    data class AlbumSelectionClicked(val album: Album) : FeedPageAction()
    object LoadFeed : FeedPageAction()
    object UserBadgePressed : FeedPageAction()
    object DismissAccountOverview : FeedPageAction()
    object LogOut : FeedPageAction()
    object RefreshAlbums : FeedPageAction()
    object ShowFeedDisplayChoice : FeedPageAction()
    object HideFeedDisplayChoice : FeedPageAction()
    object ClearSelected : FeedPageAction()
    object AskForSelectedPhotosDeletion : FeedPageAction()
    object DismissSelectedPhotosDeletion : FeedPageAction()
    object DeleteSelectedPhotos : FeedPageAction()
}
