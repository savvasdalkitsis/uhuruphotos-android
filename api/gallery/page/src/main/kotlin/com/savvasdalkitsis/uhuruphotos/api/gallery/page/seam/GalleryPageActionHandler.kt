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
package com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam

import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.ChangeGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.LoadGallery
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.SelectedPhoto
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect.ErrorLoading
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageMutation.Loading
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageMutation.ShowGalleryPage
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.GalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.gallery.view.state.PredefinedGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.properties.Delegates

class GalleryPageActionHandler(
    private val galleryRefresher: suspend (Int) -> Result<Unit>,
    private val galleryDetailsFlow: (galleryId: Int, effect: suspend (GalleryPageEffect) -> Unit) -> Flow<GalleryDetails>,
    private val galleryDetailsEmptyCheck: suspend (galleryId: Int) -> Boolean,
    private val mediaSequenceDataSource: (galleryId: Int) -> MediaSequenceDataSource,
    private val initialGalleryDisplay: (galleryId: Int) -> GalleryDisplay,
    private val galleryDisplayPersistence: suspend (galleryId:Int, PredefinedGalleryDisplay) -> Unit,
) : ActionHandler<GalleryPageState, GalleryPageEffect, GalleryPageAction, GalleryPageMutation> {

    private val loading = MutableSharedFlow<GalleryPageMutation>()
    private var galleryId by Delegates.notNull<Int>()

    override fun handleAction(
        state: GalleryPageState,
        action: GalleryPageAction,
        effect: suspend (GalleryPageEffect) -> Unit,
    ): Flow<GalleryPageMutation> = when (action) {
        is LoadGallery -> {
            merge(
                flowOf(GalleryPageMutation.ChangeGalleryDisplay(initialGalleryDisplay(action.galleryId))),
                galleryDetailsFlow(action.galleryId, effect)
                    .map(::ShowGalleryPage),
                loading,
            ).safelyOnStartIgnoring {
                galleryId = action.galleryId
                if (galleryDetailsEmptyCheck(galleryId)) {
                    refreshGallery(effect)
                }
            }
        }
        SwipeToRefresh -> flow {
            refreshGallery(effect)
        }
        is SelectedPhoto -> flow {
            effect(
                with(action) {
                    OpenPhotoDetails(
                        id = mediaItem.id,
                        center = center,
                        scale = scale,
                        video = mediaItem.isVideo,
                        mediaSequenceDataSource = mediaSequenceDataSource(galleryId)
                    )
                }
            )
        }
        NavigateBack -> flow {
            effect(GalleryPageEffect.NavigateBack)
        }
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person.id))
        }
        is ChangeGalleryDisplay -> flow {
            emit(GalleryPageMutation.ChangeGalleryDisplay(action.galleryDisplay))
            (action.galleryDisplay as? PredefinedGalleryDisplay)?.let {
                galleryDisplayPersistence(galleryId, it)
            }
        }
    }

    private suspend fun refreshGallery(effect: suspend (GalleryPageEffect) -> Unit) {
        loading.emit(Loading(true))
        val result = galleryRefresher(galleryId)
        if (result.isFailure) {
            effect(ErrorLoading)
        }
        loading.emit(Loading(false))
    }
}
