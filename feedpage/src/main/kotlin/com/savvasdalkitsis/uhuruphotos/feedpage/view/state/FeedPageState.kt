package com.savvasdalkitsis.uhuruphotos.feedpage.view.state

import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState

data class FeedPageState(
    val feedState: FeedState = FeedState(),
    val userInformationState: UserInformationState = UserInformationState(),
    val showAccountOverview: Boolean = false,
    val isRefreshing: Boolean = false,
    val showFeedDisplayChoice: Boolean = false,
    val showPhotoDeletionConfirmationDialog: Boolean = false,
    val showLogOutConfirmation: Boolean = false,
) {
    val selectedPhotoCount: Int = feedState.albums.sumOf { album ->
        album.photos.count { photo ->
            photo.isSelected
        }
    }
    val hasSelection = selectedPhotoCount > 0
    val selectedPhotos: List<Photo> = feedState.albums.flatMap { album ->
        album.photos.filter { photo ->
            photo.isSelected
        }
    }
    val shouldShowShareIcon: Boolean = selectedPhotos.let { selected ->
        selected.isNotEmpty() && selected.none(Photo::isVideo)
    }
}