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
package com.savvasdalkitsis.uhuruphotos.feature.favourites.view.implementation.seam

import com.github.michaelbull.result.getOr
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.favourites.domain.api.usecase.FavouritesUseCase
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContextFactory
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetailsState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.FavouriteMediaModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class FavouritesActionsContext @Inject constructor(
    mediaUseCase: MediaUseCase,
    favouritesUseCase: FavouritesUseCase,
    galleryActionsContextFactory: GalleryActionsContextFactory,
) {
    val galleryActionsContext = galleryActionsContextFactory.create(
        galleryRefresher = { mediaUseCase.refreshFavouriteMedia() },
        initialCollageDisplayState = { favouritesUseCase.getFavouriteMediaGalleryDisplay() },
        collageDisplayPersistence = { _, galleryDisplay ->
            favouritesUseCase.setFavouriteMediaGalleryDisplay(galleryDisplay)
        },
        shouldRefreshOnLoad = { _ -> true },
        galleryDetailsStateFlow = { _ ->
            mediaUseCase.observeFavouriteMedia()
                .mapNotNull { it.getOr(null) }
                .map { mediaItems ->
                    GalleryDetailsState(
                        title = Title.Resource(string.favourite_media),
                        clusterStates = persistentListOf(
                            ClusterState(
                                id = "favourites",
                                displayTitle = "",
                                location = null,
                                cels = mediaItems.map {
                                    it.toCel()
                                }.toImmutableList(),
                            )
                        ),
                    )
                }
        },
        lightboxSequenceDataSource = { FavouriteMediaModel },
    )
}