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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContextFactory
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.LocalAlbum
import com.savvasdalkitsis.uhuruphotos.feature.local.domain.api.usecase.LocalAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class LocalAlbumPageActionsContext @Inject constructor(
    localAlbumUseCase: LocalAlbumUseCase,
    galleryActionsContextFactory: GalleryActionsContextFactory,
) {
    val galleryActionsContext = galleryActionsContextFactory.create(
        galleryRefresher = { localAlbumUseCase.refreshLocalAlbum(it) },
        initialCollageDisplay = { localAlbumUseCase.getLocalAlbumGalleryDisplay(it) },
        collageDisplayPersistence = { id, galleryDisplay ->
            localAlbumUseCase.setLocalAlbumGalleryDisplay(id, galleryDisplay)
        },
        shouldRefreshOnLoad = { albumId ->
            localAlbumUseCase.getLocalAlbum(albumId).isEmpty()
        },
        galleryDetailsFlow = { albumId, _ ->
            localAlbumUseCase.observeLocalAlbum(albumId)
                .map { (bucket, albums) ->
                    GalleryDetails(
                        title = Title.Text(bucket.displayName),
                        clusters = albums.map { it.toCluster() },
                    )
                }
        },
        lightboxSequenceDataSource = { LocalAlbum(it) },
    )
}