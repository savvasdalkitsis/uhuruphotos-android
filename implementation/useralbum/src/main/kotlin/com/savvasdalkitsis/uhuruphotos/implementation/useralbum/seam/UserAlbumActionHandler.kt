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
import com.savvasdalkitsis.uhuruphotos.api.albumpage.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.api.seam.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.useralbum.usecase.UserAlbumsUseCase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserAlbumActionHandler @Inject constructor(
    userUseCase: UserUseCase,
    userAlbumsUseCase: UserAlbumsUseCase,
    remoteMediaUseCase: RemoteMediaUseCase,
    dateDisplayer: DateDisplayer,
) : ActionHandler<AlbumPageState, AlbumPageEffect, AlbumPageAction, AlbumPageMutation>
by AlbumPageActionHandler(
    albumRefresher = { userAlbumsUseCase.refreshUserAlbum(it) },
    initialFeedDisplay = { userAlbumsUseCase.getUserAlbumFeedDisplay(it) },
    feedDisplayPersistence = { albumId, feedDisplay ->
        userAlbumsUseCase.setUserAlbumFeedDisplay(albumId, feedDisplay)
    },
    albumDetailsEmptyCheck = { albumId ->
        userAlbumsUseCase.getUserAlbum(albumId).items.isEmpty()
    },
    albumDetailsFlow = { albumId, _ ->
        userAlbumsUseCase.observeUserAlbum(albumId)
            .map { photoEntries ->
                val favouriteThreshold = userUseCase.getUserOrRefresh()
                    .mapCatching { it.favoriteMinRating!! }
                AlbumDetails(
                    title = Title.Text(photoEntries.firstOrNull()?.title ?: ""),
                    albums = photoEntries.groupBy { entry ->
                        dateDisplayer.dateString(entry.date)
                    }.entries.map { (date, photos) ->
                        Album(
                            id = date,
                            displayTitle = date,
                            location = null,
                            photos = photos.map { photo ->
                                MediaItem(
                                    id = photo.photoId.toString(),
                                    mediaHash = photo.photoId.toString(),
                                    thumbnailUri = with(remoteMediaUseCase) {
                                        photo.photoId.toThumbnailUrlFromIdNullable()
                                    },
                                    fullResUri = with(remoteMediaUseCase) {
                                        photo.photoId.toFullSizeUrlFromIdNullable(photo.isVideo)
                                    },
                                    fallbackColor = photo.dominantColor,
                                    ratio = photo.aspectRatio ?: 1f,
                                    isFavourite = favouriteThreshold
                                        .map {
                                            (photo.rating ?: 0) >= it
                                        }
                                        .getOrElse { false },
                                    isVideo = photo.isVideo,
                                    displayDayDate = date,
                                )
                            }
                        )
                    }
                )
            }
    },
    mediaSequenceDataSource = { UserAlbum(it) }
)
