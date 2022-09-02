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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction.ChangeCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.ErrorLoading
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation.ShowGallery
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.properties.Delegates

class GalleryActionHandler(
    private val galleryRefresher: suspend (Int) -> Result<Unit>,
    private val galleryDetailsFlow: (galleryId: Int, effect: suspend (GalleryEffect) -> Unit) -> Flow<GalleryDetails>,
    private val galleryDetailsEmptyCheck: suspend (galleryId: Int) -> Boolean,
    private val lightboxSequenceDataSource: (galleryId: Int) -> LightboxSequenceDataSource,
    private val initialCollageDisplay: (galleryId: Int) -> CollageDisplay,
    private val collageDisplayPersistence: suspend (galleryId:Int, PredefinedCollageDisplay) -> Unit,
) : ActionHandler<GalleryState, GalleryEffect, GalleryAction, GalleryMutation> {

    private val loading = MutableSharedFlow<GalleryMutation>()
    private var galleryId by Delegates.notNull<Int>()

    override fun handleAction(
        state: GalleryState,
        action: GalleryAction,
        effect: suspend (GalleryEffect) -> Unit,
    ): Flow<GalleryMutation> = when (action) {
        is LoadCollage -> {
            merge(
                flowOf(GalleryMutation.ChangeCollageDisplay(initialCollageDisplay(action.collageId))),
                galleryDetailsFlow(action.collageId, effect)
                    .map(::ShowGallery),
                loading,
            ).safelyOnStartIgnoring {
                galleryId = action.collageId
                if (galleryDetailsEmptyCheck(galleryId)) {
                    refreshGallery(effect)
                }
            }
        }
        SwipeToRefresh -> flow {
            refreshGallery(effect)
        }
        is SelectedCel -> flow {
            effect(
                with(action) {
                    OpenLightbox(
                        id = cel.mediaItem.id,
                        center = center,
                        scale = scale,
                        video = cel.mediaItem.isVideo,
                        lightboxSequenceDataSource = lightboxSequenceDataSource(galleryId)
                    )
                }
            )
        }
        NavigateBack -> flow {
            effect(GalleryEffect.NavigateBack)
        }
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person.id))
        }
        is ChangeCollageDisplay -> flow {
            emit(GalleryMutation.ChangeCollageDisplay(action.collageDisplay))
            (action.collageDisplay as? PredefinedCollageDisplay)?.let {
                collageDisplayPersistence(galleryId, it)
            }
        }
    }

    private suspend fun refreshGallery(effect: suspend (GalleryEffect) -> Unit) {
        loading.emit(Loading(true))
        val result = galleryRefresher(galleryId)
        if (result.isFailure) {
            effect(ErrorLoading)
        }
        loading.emit(Loading(false))
    }
}
