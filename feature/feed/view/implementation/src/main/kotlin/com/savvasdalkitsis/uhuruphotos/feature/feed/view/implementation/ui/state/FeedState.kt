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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state

import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.PredefinedGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItemSelectionMode

internal data class FeedState(
    val galleryState: GalleryState = GalleryState(),
    val isRefreshing: Boolean = false,
    val showLibrary: Boolean = true,
    val showPhotoTrashingConfirmationDialog: Boolean = false,
) {
    val selectedPhotoCount: Int = galleryState.albums.sumOf { album ->
        album.photos.count { photo ->
            photo.selectionMode == MediaItemSelectionMode.SELECTED
        }
    }
    val hasSelection = selectedPhotoCount > 0
    val selectedMediaItem: List<MediaItem> = galleryState.albums.flatMap { album ->
        album.photos.filter { photo ->
            photo.selectionMode == MediaItemSelectionMode.SELECTED
        }
    }
    val shouldShowShareIcon: Boolean = selectedMediaItem.let { selected ->
        selected.isNotEmpty() && selected.none(MediaItem::isVideo)
    }
    val shouldShowAlbumRefreshButtons: Boolean = galleryState.galleryDisplay != PredefinedGalleryDisplay.YEARLY
}