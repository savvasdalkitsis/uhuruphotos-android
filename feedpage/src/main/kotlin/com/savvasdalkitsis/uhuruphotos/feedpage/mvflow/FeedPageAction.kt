/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feedpage.mvflow

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.albums.api.model.Album
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo

sealed class FeedPageAction {
    data class SelectedPhoto(
        val photo: Photo,
        val center: Offset,
        val scale: Float,
    ) : FeedPageAction()
    data class ChangeDisplay(val display: FeedDisplays) : FeedPageAction()
    data class PhotoLongPressed(val photo: Photo) : FeedPageAction()
    data class AlbumSelectionClicked(val album: Album) : FeedPageAction()
    object LoadFeed : FeedPageAction()
    object UserBadgePressed : FeedPageAction()
    object DismissAccountOverview : FeedPageAction()
    object AskToLogOut : FeedPageAction()
    object DismissLogOutDialog : FeedPageAction()
    object LogOut : FeedPageAction()
    object RefreshAlbums : FeedPageAction()
    object ClearSelected : FeedPageAction()
    object AskForSelectedPhotosDeletion : FeedPageAction()
    object DismissSelectedPhotosDeletion : FeedPageAction()
    object DeleteSelectedPhotos : FeedPageAction()
    object ShareSelectedPhotos : FeedPageAction()
    object EditServer : FeedPageAction()
    object SettingsClick : FeedPageAction()
}
