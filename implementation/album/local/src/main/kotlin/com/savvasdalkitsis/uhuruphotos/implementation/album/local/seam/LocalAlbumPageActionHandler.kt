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
package com.savvasdalkitsis.uhuruphotos.implementation.album.local.seam

import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageMutation
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.album.local.usecase.LocalAlbumUseCase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LocalAlbumPageActionHandler @Inject constructor(
    localAlbumUseCase: LocalAlbumUseCase,
) : ActionHandler<GalleryPageState, GalleryPageEffect, GalleryPageAction, GalleryPageMutation>
by GalleryPageActionHandler(
    galleryRefresher = { localAlbumUseCase.refreshLocalAlbum(it) },
    initialFeedDisplay = { localAlbumUseCase.getLocalAlbumFeedDisplay(it) },
    feedDisplayPersistence = { id, feedDisplay ->
        localAlbumUseCase.setLocalAlbumFeedDisplay(id, feedDisplay)
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
    mediaSequenceDataSource = { MediaSequenceDataSource.LocalAlbum(it) }
)
