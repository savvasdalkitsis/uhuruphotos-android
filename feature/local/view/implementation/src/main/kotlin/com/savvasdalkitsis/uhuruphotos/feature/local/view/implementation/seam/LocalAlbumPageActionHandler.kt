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

import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.model.ExhibitSequenceDataSource.LocalAlbum
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.feature.local.domain.api.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LocalAlbumPageActionHandler @Inject constructor(
    localAlbumUseCase: LocalAlbumUseCase,
) : ActionHandler<GalleryState, GalleryEffect, GalleryAction, GalleryMutation>
by GalleryActionHandler(
    galleryRefresher = { localAlbumUseCase.refreshLocalAlbum(it) },
    initialCollageDisplay = { localAlbumUseCase.getLocalAlbumGalleryDisplay(it) },
    collageDisplayPersistence = { id, galleryDisplay ->
        localAlbumUseCase.setLocalAlbumGalleryDisplay(id, galleryDisplay)
    },
    galleryDetailsEmptyCheck = { albumId ->
        localAlbumUseCase.getLocalAlbum(albumId).isEmpty()
    },
    galleryDetailsFlow = { albumId, _ ->
        localAlbumUseCase.observeLocalAlbum(albumId)
            .map { (bucket, albums) ->
                GalleryDetails(
                    title = Title.Text(bucket.displayName),
                    albums = albums,
                )
            }
    },
    exhibitSequenceDataSource = { LocalAlbum(it) }
)
