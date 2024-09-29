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
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContextFactory
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetailsState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.UserAlbumModel
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserAlbumActionsContext @Inject constructor(
    userAlbumDisplay: UserAlbumDisplay,
    val userAlbumUseCase: UserAlbumUseCase,
    galleryActionsContextFactory: GalleryActionsContextFactory,
    val navigator: Navigator,
    val toaster: ToasterUseCase,
) {
    val galleryActionsContext = galleryActionsContextFactory.create(
        galleryRefresher = { userAlbumUseCase.refreshUserAlbum(it) },
        initialCollageDisplayState = { userAlbumDisplay.getUserAlbumGalleryDisplay(it) },
        collageDisplayPersistence = { albumId, galleryDisplay ->
            userAlbumDisplay.setUserAlbumGalleryDisplay(albumId, galleryDisplay)
        },
        shouldRefreshOnLoad = { albumId ->
            userAlbumUseCase.getUserAlbum(albumId).mediaCollections.isEmpty()
        },
        galleryDetailsStateFlow = { albumId ->
            userAlbumUseCase.observeUserAlbum(albumId)
                .map { album ->
                    GalleryDetailsState(
                        title = Title.Text(album.title),
                        clusterStates = album.mediaCollections.map { it.toCluster() }
                            .toImmutableList()
                    )
                }
        },
        lightboxSequenceDataSource = { UserAlbumModel(it) },
    )
}
