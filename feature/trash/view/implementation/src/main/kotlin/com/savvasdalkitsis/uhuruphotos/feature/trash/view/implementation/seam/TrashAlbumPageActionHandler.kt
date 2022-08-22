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

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.model.ExhibitSequenceDataSource.Trash
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaMutation
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaDetails
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaState
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.usecase.TrashUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TrashAlbumPageActionHandler @Inject constructor(
    trashUseCase: TrashUseCase,
    settingsUseCase: com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase,
    biometricsUseCase: BiometricsUseCase
): ActionHandler<GalleriaState, GalleriaEffect, GalleriaAction, GalleriaMutation> by GalleriaActionHandler(
    galleryRefresher = { trashUseCase.refreshTrash() },
    initialGalleryDisplay = { trashUseCase.getTrashGalleryDisplay() },
    galleryDisplayPersistence = { _, galleryDisplay ->
        trashUseCase.setTrashGalleryDisplay(galleryDisplay)
    },
    galleryDetailsEmptyCheck = {
        !trashUseCase.hasTrash()
    },
    galleriaDetailsFlow = { _, effect ->
        settingsUseCase.observeBiometricsRequiredForTrashAccess()
            .flatMapLatest { biometricsRequired ->
                val proceed = when {
                    biometricsRequired -> biometricsUseCase.authenticate(
                        string.authenticate,
                        string.authenticate_for_access_to_trash,
                        string.authenticate_for_access_to_trash_description,
                        true,
                    )
                    else -> Result.success(Unit)
                }
                if (proceed.isFailure) {
                    flow {
                        effect(NavigateBack)
                    }
                } else {
                    trashUseCase.observeTrashAlbums()
                        .map { albums ->
                            GalleriaDetails(
                                title = Title.Resource(string.trash),
                                albums = albums.map { album ->
                                    Album(
                                        id = album.id,
                                        displayTitle = album.displayTitle,
                                        location = album.location,
                                        photos = album.photos,
                                    )
                                }
                            )
                        }
                }
            }

    },
    exhibitSequenceDataSource = { Trash },
)