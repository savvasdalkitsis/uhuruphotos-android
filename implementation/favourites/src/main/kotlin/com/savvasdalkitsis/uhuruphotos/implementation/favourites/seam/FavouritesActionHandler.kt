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
package com.savvasdalkitsis.uhuruphotos.implementation.favourites.seam

import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageMutation
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumDetails
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource.FavouriteMedia
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.favourites.usecase.FavouritesUseCase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class FavouritesActionHandler @Inject constructor(
    mediaUseCase: MediaUseCase,
    favouritesUseCase: FavouritesUseCase,
) : ActionHandler<AlbumPageState, AlbumPageEffect, AlbumPageAction, AlbumPageMutation>
by AlbumPageActionHandler(
    albumRefresher = { mediaUseCase.refreshFavouriteMedia() },
    initialFeedDisplay = { favouritesUseCase.getFavouritesFeedDisplay() },
    feedDisplayPersistence = { _, feedDisplay ->
        favouritesUseCase.setFavouritesFeedDisplay(feedDisplay)
    },
    albumDetailsEmptyCheck = { _ ->
        mediaUseCase.getFavouriteMediaCount().getOrDefault(0) > 0
    },
    albumDetailsFlow = { _, _ ->
        mediaUseCase.observeFavouriteMedia()
            .mapNotNull { it.getOrNull() }
            .map { mediaItems ->
                AlbumDetails(
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
