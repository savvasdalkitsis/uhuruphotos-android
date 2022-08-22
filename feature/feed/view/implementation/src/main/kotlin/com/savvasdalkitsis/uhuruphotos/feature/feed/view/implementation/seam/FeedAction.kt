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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem

internal sealed class FeedAction {
    data class SelectedPhoto(
        val mediaItem: MediaItem,
        val center: Offset,
        val scale: Float,
    ) : FeedAction()
    data class ChangeDisplay(val display: PredefinedCollageDisplay) : FeedAction()
    data class PhotoLongPressed(val mediaItem: MediaItem) : FeedAction()
    data class AlbumSelectionClicked(val album: Album) : FeedAction()
    data class AlbumRefreshClicked(val album: Album) : FeedAction()
    object LoadFeed : FeedAction()
    object RefreshAlbums : FeedAction()
    object ClearSelected : FeedAction()
    object AskForSelectedPhotosTrashing : FeedAction()
    object DismissSelectedPhotosTrashing : FeedAction()
    object TrashSelectedPhotos : FeedAction()
    object ShareSelectedPhotos : FeedAction()
    object DownloadSelectedPhotos : FeedAction()
}
