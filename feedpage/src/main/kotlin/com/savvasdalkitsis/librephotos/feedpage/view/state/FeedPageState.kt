package com.savvasdalkitsis.librephotos.feedpage.view.state

import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.photos.model.Photo
import com.savvasdalkitsis.librephotos.userbadge.view.state.UserBadgeState

data class FeedPageState(
    val feedState: FeedState = FeedState(),
    val userBadgeState: UserBadgeState = UserBadgeState(),
    val showAccountOverview: Boolean = false,
    val isRefreshing: Boolean = false,
    val showFeedDisplayChoice: Boolean = false,
    val showPhotoDeletionConfirmationDialog: Boolean = false,
) {
    val selectedPhotoCount: Int = feedState.albums.sumOf { album ->
        album.photos.count { photo ->
            photo.isSelected
        }
    }
    val selectedPhotos: List<Photo> = feedState.albums.flatMap { album ->
        album.photos.filter { photo ->
            photo.isSelected
        }
    }
    val shouldShowShareIcon: Boolean = selectedPhotos.let { selected ->
        selected.isNotEmpty() && selected.none(Photo::isVideo)
    }
}