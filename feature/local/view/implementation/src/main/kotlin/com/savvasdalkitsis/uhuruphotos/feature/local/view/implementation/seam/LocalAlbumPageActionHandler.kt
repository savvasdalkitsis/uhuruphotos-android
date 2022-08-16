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

import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomAction
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffect
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomMutation
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomDetails
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomState
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.local.domain.api.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LocalAlbumPageActionHandler @Inject constructor(
    localAlbumUseCase: LocalAlbumUseCase,
) : ActionHandler<ShowroomState, ShowroomEffect, ShowroomAction, ShowroomMutation>
by ShowroomActionHandler(
    galleryRefresher = { localAlbumUseCase.refreshLocalAlbum(it) },
    initialGalleryDisplay = { localAlbumUseCase.getLocalAlbumGalleryDisplay(it) },
    galleryDisplayPersistence = { id, galleryDisplay ->
        localAlbumUseCase.setLocalAlbumGalleryDisplay(id, galleryDisplay)
    },
    galleryDetailsEmptyCheck = { albumId ->
        localAlbumUseCase.getLocalAlbum(albumId).isEmpty()
    },
    showroomDetailsFlow = { albumId, _ ->
        localAlbumUseCase.observeLocalAlbum(albumId)
            .map { (bucket, albums) ->
                ShowroomDetails(
                    title = Title.Text(bucket.displayName),
                    albums = albums,
                )
            }
    },
    mediaSequenceDataSource = { MediaSequenceDataSource.LocalAlbum(it) }
)
