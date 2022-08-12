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

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.PredefinedGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.view.state.FeedPageState

internal sealed class FeedPageMutation(
    mutation: Mutation<FeedPageState>,
) : Mutation<FeedPageState> by mutation {

    object Loading : FeedPageMutation({
        it.copyFeed { copy(isLoading = true) }
    })

    object StartRefreshing : FeedPageMutation({
        it.copy(isRefreshing = true)
    })

    object StopRefreshing : FeedPageMutation({
        it.copy(isRefreshing = false)
    })

    object ShowTrashingConfirmationDialog : FeedPageMutation({
        it.copy(showPhotoTrashingConfirmationDialog = true)
    })

    object HideTrashingConfirmationDialog : FeedPageMutation({
        it.copy(showPhotoTrashingConfirmationDialog = false)
    })

    object ShowNoPhotosFound : FeedPageMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = true, albums = emptyList()) }
    })

    data class ShowAlbums(val albums: List<Album>) : FeedPageMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = false, albums = albums) }
    }) {
        override fun toString() = "Showing ${albums.size} albums"
    }

    data class ChangeDisplay(val display: PredefinedGalleryDisplay) : FeedPageMutation({
        it.copyFeed { copy(galleryDisplay = display) }
    })

    data class ShowLibrary(val showLibrary: Boolean) : FeedPageMutation({
        it.copy(showLibrary = showLibrary)
    })
}

private fun FeedPageState.copyFeed(galleryStateMutation: GalleryState.() -> GalleryState) =
    copy(galleryState = galleryStateMutation(galleryState))
