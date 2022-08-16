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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.auto.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageMutation
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaId
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.api.media.remote.domain.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.auto.state.AutoAlbumGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.album.auto.usecase.AutoAlbumsUseCase
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AutoAlbumActionHandler @Inject constructor(
    autoAlbumsUseCase: AutoAlbumsUseCase,
    remoteMediaUseCase: RemoteMediaUseCase,
    dateDisplayer: DateDisplayer,
) : ActionHandler<GalleryPageState, GalleryPageEffect, GalleryPageAction, GalleryPageMutation>
by GalleryPageActionHandler(
    galleryRefresher = { autoAlbumsUseCase.refreshAutoAlbum(it) },
    initialGalleryDisplay = { AutoAlbumGalleryDisplay },
    galleryDisplayPersistence = { _, _ -> },
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
    mediaSequenceDataSource = { AutoAlbum(it) }
)
