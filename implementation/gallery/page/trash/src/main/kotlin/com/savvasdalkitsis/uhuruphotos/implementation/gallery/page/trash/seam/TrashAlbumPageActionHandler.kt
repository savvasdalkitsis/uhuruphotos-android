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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.trash.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageAction
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageActionHandler
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.seam.GalleryPageMutation
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.GalleryPageState
import com.savvasdalkitsis.uhuruphotos.api.gallery.page.view.state.Title
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaSequenceDataSource.Trash
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.trash.usecase.TrashUseCase
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TrashAlbumPageActionHandler @Inject constructor(
    trashUseCase: TrashUseCase,
    settingsUseCase: SettingsUseCase,
    biometricsUseCase: BiometricsUseCase
): ActionHandler<GalleryPageState, GalleryPageEffect, GalleryPageAction, GalleryPageMutation> by GalleryPageActionHandler(
    galleryRefresher = { trashUseCase.refreshTrash() },
    initialGalleryDisplay = { trashUseCase.getTrashGalleryDisplay() },
    galleryDisplayPersistence = { _, galleryDisplay ->
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
                        .map { albums ->
                            GalleryDetails(
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
    mediaSequenceDataSource = { Trash },
)