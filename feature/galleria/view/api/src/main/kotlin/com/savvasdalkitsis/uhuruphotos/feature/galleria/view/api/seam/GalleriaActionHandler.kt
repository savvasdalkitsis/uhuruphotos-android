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
package com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam

import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction.ChangeGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction.LoadGallery
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction.PersonSelected
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction.SelectedMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction.SwipeToRefresh
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect.ErrorLoading
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect.OpenMedia
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaMutation.ShowGalleria
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaDetails
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.PredefinedGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.coroutines.api.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlin.properties.Delegates

class GalleriaActionHandler(
    private val galleryRefresher: suspend (Int) -> Result<Unit>,
    private val galleriaDetailsFlow: (galleryId: Int, effect: suspend (GalleriaEffect) -> Unit) -> Flow<GalleriaDetails>,
    private val galleryDetailsEmptyCheck: suspend (galleryId: Int) -> Boolean,
    private val mediaSequenceDataSource: (galleryId: Int) -> MediaSequenceDataSource,
    private val initialGalleryDisplay: (galleryId: Int) -> GalleryDisplay,
    private val galleryDisplayPersistence: suspend (galleryId:Int, PredefinedGalleryDisplay) -> Unit,
) : ActionHandler<GalleriaState, GalleriaEffect, GalleriaAction, GalleriaMutation> {

    private val loading = MutableSharedFlow<GalleriaMutation>()
    private var galleryId by Delegates.notNull<Int>()

    override fun handleAction(
        state: GalleriaState,
        action: GalleriaAction,
        effect: suspend (GalleriaEffect) -> Unit,
    ): Flow<GalleriaMutation> = when (action) {
        is LoadGallery -> {
            merge(
                flowOf(GalleriaMutation.ChangeGalleryDisplay(initialGalleryDisplay(action.galleryId))),
                galleriaDetailsFlow(action.galleryId, effect)
                    .map(::ShowGalleria),
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
        is SelectedMediaItem -> flow {
            effect(
                with(action) {
                    OpenMedia(
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
            effect(GalleriaEffect.NavigateBack)
        }
        is PersonSelected -> flow {
            effect(NavigateToPerson(action.person.id))
        }
        is ChangeGalleryDisplay -> flow {
            emit(GalleriaMutation.ChangeGalleryDisplay(action.galleryDisplay))
            (action.galleryDisplay as? PredefinedGalleryDisplay)?.let {
                galleryDisplayPersistence(galleryId, it)
            }
        }
    }

    private suspend fun refreshGallery(effect: suspend (GalleriaEffect) -> Unit) {
        loading.emit(Loading(true))
        val result = galleryRefresher(galleryId)
        if (result.isFailure) {
            effect(ErrorLoading)
        }
        loading.emit(Loading(false))
    }
}
