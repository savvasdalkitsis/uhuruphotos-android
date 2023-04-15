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

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.state.AutoAlbumCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.AutoAlbum
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstance
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSyncState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AutoAlbumActionsContext @Inject constructor(
    autoAlbumUseCase: AutoAlbumUseCase,
    remoteMediaUseCase: RemoteMediaUseCase,
    dateDisplayer: DateDisplayer,
    flowSharedPreferences: FlowSharedPreferences,
) : GalleryActionsContext(
    galleryRefresher = { autoAlbumUseCase.refreshAutoAlbum(it) },
    initialCollageDisplay = { AutoAlbumCollageDisplay },
    collageDisplayPersistence = { _, _ -> },
    galleryDetailsEmptyCheck = { albumId ->
        autoAlbumUseCase.getAutoAlbum(albumId).isEmpty()
    },
    galleryDetailsFlow = { albumId, _ ->
        autoAlbumUseCase.observeAutoAlbum(albumId)
            .map { (photoEntries, people) ->
                GalleryDetails(
                    title = Title.Text(photoEntries.firstOrNull()?.title ?: ""),
                    people = with(remoteMediaUseCase) {
                        people.map { person ->
                            person.toPerson { it.toRemoteUrl() }
                        }
                    },
                    clusters = photoEntries.groupBy { entry ->
                        dateDisplayer.dateString(entry.timestamp)
                    }.entries.map { (date, photos) ->
                        Cluster(
                            id = date,
                            unformattedDate = photos.firstOrNull()?.timestamp,
                            displayTitle = date,
                            location = null,
                            cels = photos.map {
                                MediaItemInstance(
                                    id = MediaId.Remote(it.photoId.toString()),
                                    mediaHash = it.photoId.toString(),
                                    thumbnailUri = with(remoteMediaUseCase) {
                                        it.photoId.toThumbnailUrlFromIdNullable()
                                    },
                                    fullResUri = with(remoteMediaUseCase) {
                                        it.photoId.toFullSizeUrlFromIdNullable(it.video ?: false)
                                    },
                                    displayDayDate = date,
                                    sortableDate = it.timestamp,
                                    isFavourite = it.isFavorite ?: false,
                                    isVideo = it.video ?: false,
                                    syncState = MediaItemSyncState.REMOTE_ONLY,
                                ).toCel()
                            }
                        )
                    }
                )
            }
    },
    lightboxSequenceDataSource = { AutoAlbum(it) },
    flowSharedPreferences = flowSharedPreferences,
)
