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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.state.AutoAlbumCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AutoAlbumActionHandler @Inject constructor(
    autoAlbumsUseCase: AutoAlbumsUseCase,
    remoteMediaUseCase: RemoteMediaUseCase,
    dateDisplayer: DateDisplayer,
) : ActionHandler<GalleryState, GalleryEffect, GalleryAction, GalleryMutation>
by GalleryActionHandler(
    galleryRefresher = { autoAlbumsUseCase.refreshAutoAlbum(it) },
    initialCollageDisplay = { AutoAlbumCollageDisplay },
    collageDisplayPersistence = { _, _ -> },
    galleryDetailsEmptyCheck = { albumId ->
        autoAlbumsUseCase.getAutoAlbum(albumId).items.isEmpty()
    },
    galleryDetailsFlow = { albumId, _ ->
        autoAlbumsUseCase.observeAutoAlbum(albumId)
            .map { (photoEntries, people) ->
                GalleryDetails(
                    title = Title.Text(photoEntries.firstOrNull()?.title ?: ""),
                    people = with(remoteMediaUseCase) {
                        people.map { person ->
                            person.toPerson { it.toRemoteUrl() }
                        }
                    },
                    albums = photoEntries.groupBy { entry ->
                        dateDisplayer.dateString(entry.timestamp)
                    }.entries.map { (date, photos) ->
                        Album(
                            id = date,
                            displayTitle = date,
                            location = null,
                            photos = photos.map {
                                MediaItem(
                                    id = MediaId.Remote(it.photoId.toString()),
                                    mediaHash = it.photoId.toString(),
                                    thumbnailUri = with(remoteMediaUseCase) {
                                        it.photoId.toThumbnailUrlFromIdNullable()
                                    },
                                    fullResUri = with(remoteMediaUseCase) {
                                        it.photoId.toFullSizeUrlFromIdNullable(it.video ?: false)
                                    },
                                    displayDayDate = date,
                                    isFavourite = it.isFavorite ?: false,
                                    isVideo = it.video ?: false,
                                )
                            }
                        )
                    }
                )
            }
    },
    lightboxSequenceDataSource = { AutoAlbum(it) }
)
