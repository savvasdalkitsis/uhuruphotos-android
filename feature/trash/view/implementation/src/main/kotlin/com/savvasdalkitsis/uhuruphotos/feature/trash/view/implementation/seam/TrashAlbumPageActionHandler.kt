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
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Trash
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.usecase.TrashUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TrashAlbumPageActionHandler @Inject constructor(
    trashUseCase: TrashUseCase,
    settingsUseCase: SettingsUseCase,
    biometricsUseCase: BiometricsUseCase
): ActionHandler<GalleryState, GalleryEffect, GalleryAction, GalleryMutation> by GalleryActionHandler(
    galleryRefresher = { trashUseCase.refreshTrash() },
    initialCollageDisplay = { trashUseCase.getTrashGalleryDisplay() },
    collageDisplayPersistence = { _, galleryDisplay ->
        trashUseCase.setTrashGalleryDisplay(galleryDisplay)
    },
    galleryDetailsEmptyCheck = {
        !trashUseCase.hasTrash()
    },
    galleryDetailsFlow = { _, effect ->
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
                        .map { mediaCollections ->
                            GalleryDetails(
                                title = Title.Resource(string.trash),
                                albums = mediaCollections.map { mediaCollection ->
                                    Album(
                                        id = mediaCollection.id,
                                        displayTitle = mediaCollection.displayTitle,
                                        location = mediaCollection.location,
                                        photos = mediaCollection.mediaItems,
                                    )
                                }
                            )
                        }
                }
            }

    },
    lightboxSequenceDataSource = { Trash },
)