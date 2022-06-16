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
package com.savvasdalkitsis.uhuruphotos.implementation.useralbum.seam

import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageAction
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageEffect
import com.savvasdalkitsis.uhuruphotos.api.albumpage.seam.AlbumPageMutation
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumDetails
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.AlbumPageState
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.Photo
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.api.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.useralbum.usecase.UserAlbumsUseCase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserAlbumActionHandler @Inject constructor(
    userUseCase: UserUseCase,
    userAlbumsUseCase: UserAlbumsUseCase,
    photosUseCase: PhotosUseCase,
    dateDisplayer: DateDisplayer,
) : ActionHandler<AlbumPageState, AlbumPageEffect, AlbumPageAction, AlbumPageMutation>
by AlbumPageActionHandler(
    albumRefresher = { userAlbumsUseCase.refreshUserAlbum(it) },
    initialFeedDisplay = { userAlbumsUseCase.getUserAlbumFeedDisplay(it) },
    feedDisplayPersistence = { albumId, feedDisplay ->
        userAlbumsUseCase.setUserAlbumFeedDisplay(albumId, feedDisplay)
    },
    albumDetailsFlow = { albumId ->
        userAlbumsUseCase.observeUserAlbum(albumId)
            .map { photoEntries ->
                val favouriteThreshold = userUseCase.getUserOrRefresh()?.favoriteMinRating
                AlbumDetails(
                    title = photoEntries.firstOrNull()?.title ?: "",
                    albums = photoEntries.groupBy { entry ->
                        dateDisplayer.dateString(entry.date)
                    }.entries.map { (date, photos) ->
                        Album(
                            id = date,
                            date = date,
                            location = null,
                            photos = photos.map {
                                Photo(
                                    id = it.photoId.toString(),
                                    thumbnailUrl = with(photosUseCase) {
                                        it.photoId.toThumbnailUrlFromIdNullable()
                                    },
                                    fallbackColor = it.dominantColor,
                                    ratio = it.aspectRatio ?: 1f,
                                    isFavourite = favouriteThreshold != null
                                            && (it.rating ?: 0) >= favouriteThreshold,
                                    isVideo = it.isVideo,
                                )
                            }
                        )
                    }
                )
            }
    },
    photoSequenceDataSource = { UserAlbum(it) }
)
