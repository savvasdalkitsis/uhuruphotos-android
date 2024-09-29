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

import com.github.michaelbull.result.mapOr
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.domain.api.usecase.AutoAlbumUseCase
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.ui.state.AutoAlbumCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContextFactory
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetailsState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.AutoAlbumModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemHashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemInstanceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.toPerson
import com.savvasdalkitsis.uhuruphotos.feature.user.domain.api.usecase.UserUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class AutoAlbumActionsContext @Inject constructor(
    val autoAlbumUseCase: AutoAlbumUseCase,
    serverUseCase: ServerUseCase,
    dateDisplayer: DateDisplayer,
    galleryActionsContextFactory: GalleryActionsContextFactory,
    userUseCase: UserUseCase,
    val navigator: Navigator,
    val toaster: ToasterUseCase,
) {
    val galleryActionsContext = galleryActionsContextFactory.create(
        galleryRefresher = { autoAlbumUseCase.refreshAutoAlbum(it) },
        initialCollageDisplayState = { AutoAlbumCollageDisplayState },
        collageDisplayPersistence = { _, _ -> },
        shouldRefreshOnLoad = { albumId ->
            autoAlbumUseCase.getAutoAlbum(albumId).isEmpty()
        },
        galleryDetailsStateFlow = { albumId ->
            val serverUrl = serverUseCase.getServerUrl()!!
            autoAlbumUseCase.observeAutoAlbumWithPeople(albumId)
                .mapNotNull { (photoEntries, people) ->
                    userUseCase.getRemoteUserOrRefresh()
                        .mapOr(null) { user ->
                            GalleryDetailsState(
                                title = Title.Text(photoEntries.firstOrNull()?.title ?: ""),
                                people = people.map { person ->
                                    person.toPerson { "$serverUrl$it" }
                                }.toImmutableList(),
                                clusterStates = photoEntries.groupBy { entry ->
                                    dateDisplayer.dateString(entry.timestamp)
                                }.entries.map { (date, photos) ->
                                    ClusterState(
                                        id = date,
                                        unformattedDate = photos.firstOrNull()?.timestamp,
                                        displayTitle = date,
                                        location = null,
                                        cels = photos.map {
                                            MediaItemInstanceModel(
                                                id = MediaIdModel.RemoteIdModel(
                                                    it.photoId.toString(),
                                                    it.video ?: false
                                                ),
                                                mediaHash = MediaItemHashModel.fromRemoteMediaHash(
                                                    it.photoId.toString(),
                                                    user.id
                                                ),
                                                displayDayDate = date,
                                                sortableDate = it.timestamp,
                                                isFavourite = it.isFavorite ?: false,
                                            ).toCel()
                                        }.toImmutableList()
                                    )
                                }.toImmutableList()
                            )
                        }
                }
        },
        lightboxSequenceDataSource = { AutoAlbumModel(it) },
    )
}
