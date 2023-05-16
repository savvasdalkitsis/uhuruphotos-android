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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.api.usecase.UserAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserAlbumActionsContext @Inject constructor(
    userAlbumDisplay: UserAlbumDisplay,
    userAlbumUseCase: UserAlbumUseCase,
    preferences: Preferences,
) : GalleryActionsContext(
    galleryRefresher = { userAlbumUseCase.refreshUserAlbum(it) },
    initialCollageDisplay = { userAlbumDisplay.getUserAlbumGalleryDisplay(it) },
    collageDisplayPersistence = { albumId, galleryDisplay ->
        userAlbumDisplay.setUserAlbumGalleryDisplay(albumId, galleryDisplay)
    },
    galleryDetailsEmptyCheck = { albumId ->
        userAlbumUseCase.getUserAlbum(albumId).mediaCollections.isEmpty()
    },
    galleryDetailsFlow = { albumId, _ ->
        userAlbumUseCase.observeUserAlbum(albumId)
            .map { album ->
                GalleryDetails(
                    title = Title.Text(album.title),
                    clusters = album.mediaCollections.map { it.toCluster() }
                )
            }
    },
    lightboxSequenceDataSource = { UserAlbum(it) },
    preferences = preferences,
)
