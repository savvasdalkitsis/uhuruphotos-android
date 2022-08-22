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

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.db.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.usecase.UserAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.model.ExhibitSequenceDataSource.UserAlbum
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserAlbumActionHandler @Inject constructor(
    userUseCase: UserUseCase,
    userAlbumsUseCase: UserAlbumsUseCase,
    remoteMediaUseCase: RemoteMediaUseCase,
    dateDisplayer: DateDisplayer,
) : ActionHandler<GalleryState, GalleryEffect, GalleryAction, GalleryMutation>
by GalleryActionHandler(
    galleryRefresher = { userAlbumsUseCase.refreshUserAlbum(it) },
    initialCollageDisplay = { userAlbumsUseCase.getUserAlbumGalleryDisplay(it) },
    collageDisplayPersistence = { albumId, galleryDisplay ->
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
    exhibitSequenceDataSource = { UserAlbum(it) }
)
