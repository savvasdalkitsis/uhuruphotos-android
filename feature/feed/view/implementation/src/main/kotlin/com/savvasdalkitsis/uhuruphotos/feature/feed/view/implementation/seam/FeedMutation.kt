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

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

internal sealed class FeedMutation(
    mutation: Mutation<FeedState>,
) : Mutation<FeedState> by mutation {

    object Loading : FeedMutation({
        it.copyFeed { copy(isLoading = true) }
    })

    object StartRefreshing : FeedMutation({
        it.copy(isRefreshing = true)
    })

    object StopRefreshing : FeedMutation({
        it.copy(isRefreshing = false)
    })

    object ShowTrashingConfirmationDialog : FeedMutation({
        it.copy(showPhotoTrashingConfirmationDialog = true)
    })

    object HideTrashingConfirmationDialog : FeedMutation({
        it.copy(showPhotoTrashingConfirmationDialog = false)
    })

    object ShowNoPhotosFound : FeedMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = true, albums = emptyList()) }
    })

    data class ShowAlbums(val albums: List<Album>) : FeedMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = false, albums = albums) }
    }) {
        override fun toString() = "Showing ${albums.size} albums"
    }

    data class ChangeDisplay(val display: PredefinedCollageDisplay) : FeedMutation({
        it.copyFeed { copy(collageDisplay = display) }
    })

    data class ShowLibrary(val showLibrary: Boolean) : FeedMutation({
        it.copy(showLibrary = showLibrary)
    })
}

private fun FeedState.copyFeed(collageStateMutation: CollageState.() -> CollageState) =
    copy(collageState = collageStateMutation(collageState))
