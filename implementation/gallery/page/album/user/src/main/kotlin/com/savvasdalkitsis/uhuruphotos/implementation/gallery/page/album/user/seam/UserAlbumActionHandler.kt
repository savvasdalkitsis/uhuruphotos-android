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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.user.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageMutation
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaId
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.user.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.user.usecase.UserAlbumsUseCase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserAlbumActionHandler @Inject constructor(
    userUseCase: UserUseCase,
    userAlbumsUseCase: UserAlbumsUseCase,
    remoteMediaUseCase: RemoteMediaUseCase,
    dateDisplayer: DateDisplayer,
) : ActionHandler<GalleryPageState, GalleryPageEffect, GalleryPageAction, GalleryPageMutation>
by GalleryPageActionHandler(
    galleryRefresher = { userAlbumsUseCase.refreshUserAlbum(it) },
    initialGalleryDisplay = { userAlbumsUseCase.getUserAlbumGalleryDisplay(it) },
    galleryDisplayPersistence = { albumId, galleryDisplay ->
        userAlbumsUseCase.setUserAlbumGalleryDisplay(albumId, galleryDisplay)
    },
    galleryDetailsEmptyCheck = { albumId ->
        userAlbumsUseCase.getUserAlbum(albumId).items.isEmpty()
    },
    galleryDetailsFlow = { albumId, _ ->
        userAlbumsUseCase.observeUserAlbum(albumId)
            .map { photoEntries ->
                val favouriteThreshold = userUseCase.getUserOrRefresh()
                    .mapCatching { it.favoriteMinRating!! }
                GalleryDetails(
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
                                    id = MediaId.Remote(photo.photoId.toString()),
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
