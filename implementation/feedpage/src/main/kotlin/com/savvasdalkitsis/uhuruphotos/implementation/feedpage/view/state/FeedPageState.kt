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
package com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view.state

import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.api.feed.view.state.FeedState
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.SelectionMode

internal data class FeedPageState(
    val feedState: FeedState = FeedState(),
    val isRefreshing: Boolean = false,
    val showLibrary: Boolean = true,
    val showPhotoTrashingConfirmationDialog: Boolean = false,
) {
    val selectedPhotoCount: Int = feedState.albums.sumOf { album ->
        album.photos.count { photo ->
            photo.selectionMode == SelectionMode.SELECTED
        }
    }
    val hasSelection = selectedPhotoCount > 0
    val selectedPhotos: List<Photo> = feedState.albums.flatMap { album ->
        album.photos.filter { photo ->
            photo.selectionMode == SelectionMode.SELECTED
        }
    }
    val shouldShowShareIcon: Boolean = selectedPhotos.let { selected ->
        selected.isNotEmpty() && selected.none(Photo::isVideo)
    }
    val shouldShowAlbumRefreshButtons: Boolean = feedState.feedDisplay != FeedDisplays.YEARLY
}