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

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomAction
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffect
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomMutation
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomDetails
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.ShowroomState
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource.FavouriteMedia
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.favourites.domain.api.usecase.FavouritesUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class FavouritesActionHandler @Inject constructor(
    mediaUseCase: MediaUseCase,
    favouritesUseCase: FavouritesUseCase,
) : ActionHandler<ShowroomState, ShowroomEffect, ShowroomAction, ShowroomMutation>
by ShowroomActionHandler(
    galleryRefresher = { mediaUseCase.refreshFavouriteMedia() },
    initialGalleryDisplay = { favouritesUseCase.getFavouriteMediaGalleryDisplay() },
    galleryDisplayPersistence = { _, galleryDisplay ->
        favouritesUseCase.setFavouriteMediaGalleryDisplay(galleryDisplay)
    },
    galleryDetailsEmptyCheck = { _ ->
        mediaUseCase.getFavouriteMediaCount().getOrDefault(0) > 0
    },
    showroomDetailsFlow = { _, _ ->
        mediaUseCase.observeFavouriteMedia()
            .mapNotNull { it.getOrNull() }
            .map { mediaItems ->
                ShowroomDetails(
                    title = Title.Resource(string.favourite_media),
                    albums = listOf(Album(
                        id = "favourites",
                        displayTitle = "",
                        location = null,
                        photos = mediaItems,
                    )),
                )
            }
    },
    mediaSequenceDataSource = { FavouriteMedia }
)
