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
package com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.PredefinedGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem

internal sealed class FeedPageAction {
    data class SelectedPhoto(
        val mediaItem: MediaItem,
        val center: Offset,
        val scale: Float,
    ) : FeedPageAction()
    data class ChangeDisplay(val display: PredefinedGalleryDisplay) : FeedPageAction()
    data class PhotoLongPressed(val mediaItem: MediaItem) : FeedPageAction()
    data class AlbumSelectionClicked(val album: Album) : FeedPageAction()
    data class AlbumRefreshClicked(val album: Album) : FeedPageAction()
    object LoadFeed : FeedPageAction()
    object RefreshAlbums : FeedPageAction()
    object ClearSelected : FeedPageAction()
    object AskForSelectedPhotosTrashing : FeedPageAction()
    object DismissSelectedPhotosTrashing : FeedPageAction()
    object TrashSelectedPhotos : FeedPageAction()
    object ShareSelectedPhotos : FeedPageAction()
    object DownloadSelectedPhotos : FeedPageAction()
}
