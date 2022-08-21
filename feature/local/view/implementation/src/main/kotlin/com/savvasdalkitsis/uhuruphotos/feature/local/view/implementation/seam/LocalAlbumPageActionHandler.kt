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
package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaMutation
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaDetails
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaState
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.feature.local.domain.api.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LocalAlbumPageActionHandler @Inject constructor(
    localAlbumUseCase: LocalAlbumUseCase,
) : ActionHandler<GalleriaState, GalleriaEffect, GalleriaAction, GalleriaMutation>
by GalleriaActionHandler(
    galleryRefresher = { localAlbumUseCase.refreshLocalAlbum(it) },
    initialGalleryDisplay = { localAlbumUseCase.getLocalAlbumGalleryDisplay(it) },
    galleryDisplayPersistence = { id, galleryDisplay ->
        localAlbumUseCase.setLocalAlbumGalleryDisplay(id, galleryDisplay)
    },
    galleryDetailsEmptyCheck = { albumId ->
        localAlbumUseCase.getLocalAlbum(albumId).isEmpty()
    },
    galleriaDetailsFlow = { albumId, _ ->
        localAlbumUseCase.observeLocalAlbum(albumId)
            .map { (bucket, albums) ->
                GalleriaDetails(
                    title = Title.Text(bucket.displayName),
                    albums = albums,
                )
            }
    },
    mediaSequenceDataSource = { MediaSequenceDataSource.LocalAlbum(it) }
)
