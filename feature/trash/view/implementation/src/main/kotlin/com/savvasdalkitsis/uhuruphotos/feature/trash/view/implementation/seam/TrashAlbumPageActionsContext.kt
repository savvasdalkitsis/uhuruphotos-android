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
package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam

import com.github.michaelbull.result.Ok
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContextFactory
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetailsState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSourceModel.TrashModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.usecase.TrashUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class TrashAlbumPageActionsContext @Inject constructor(
    trashUseCase: TrashUseCase,
    settingsUseCase: SettingsUseCase,
    biometricsUseCase: BiometricsUseCase,
    navigator: Navigator,
    galleryActionsContextFactory: GalleryActionsContextFactory,
) {
    val galleryActionsContext = galleryActionsContextFactory.create(
        galleryRefresher = { trashUseCase.refreshTrash() },
        initialCollageDisplayState = { trashUseCase.getTrashGalleryDisplay() },
        collageDisplayPersistence = { _, galleryDisplay ->
            trashUseCase.setTrashGalleryDisplay(galleryDisplay)
        },
        shouldRefreshOnLoad = {
            !trashUseCase.hasTrash()
        },
        galleryDetailsStateFlow = { _ ->
            settingsUseCase.observeBiometricsRequiredForTrashAccess()
                .flatMapLatest { biometricsRequired ->
                    val proceed = when {
                        biometricsRequired -> biometricsUseCase.authenticate(
                            string.authenticate,
                            string.authenticate_for_access_to_trash,
                            string.authenticate_for_access_to_trash_description,
                            true,
                        )

                        else -> Ok(Unit)
                    }
                    if (proceed.isErr) {
                        flow {
                            navigator.navigateUp()
                        }
                    } else {
                        trashUseCase.observeTrashAlbums()
                            .map { mediaCollections ->
                                GalleryDetailsState(
                                    title = Title.Resource(string.trash),
                                    clusterStates = mediaCollections.map { mediaCollection ->
                                        ClusterState(
                                            id = mediaCollection.id,
                                            unformattedDate = mediaCollection.unformattedDate,
                                            displayTitle = mediaCollection.displayTitle,
                                            location = mediaCollection.location,
                                            cels = mediaCollection.mediaItems.map {
                                                it.toCel()
                                            }.toImmutableList(),
                                        )
                                    }.toImmutableList()
                                )
                            }
                    }
                }

        },
        lightboxSequenceDataSource = { TrashModel },
    )
}