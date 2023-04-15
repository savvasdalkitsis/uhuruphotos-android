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

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.favourites.domain.api.usecase.FavouritesUseCase
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.FavouriteMedia
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class FavouritesActionsContext @Inject constructor(
    mediaUseCase: MediaUseCase,
    favouritesUseCase: FavouritesUseCase,
    flowSharedPreferences: FlowSharedPreferences,
) : GalleryActionsContext(
    galleryRefresher = { mediaUseCase.refreshFavouriteMedia() },
    initialCollageDisplay = { favouritesUseCase.getFavouriteMediaGalleryDisplay() },
    collageDisplayPersistence = { _, galleryDisplay ->
        favouritesUseCase.setFavouriteMediaGalleryDisplay(galleryDisplay)
    },
    galleryDetailsEmptyCheck = { _ ->
        mediaUseCase.getFavouriteMediaCount().getOrDefault(0) > 0
    },
    galleryDetailsFlow = { _, _ ->
        mediaUseCase.observeFavouriteMedia()
            .mapNotNull { it.getOrNull() }
            .map { mediaItems ->
                GalleryDetails(
                    title = Title.Resource(string.favourite_media),
                    clusters = listOf(
                        Cluster(
                            id = "favourites",
                            displayTitle = "",
                            location = null,
                            cels = mediaItems.map {
                                it.toCel()
                            },
                        )
                    ),
                )
            }
    },
    lightboxSequenceDataSource = { FavouriteMedia },
    flowSharedPreferences = flowSharedPreferences,
)
