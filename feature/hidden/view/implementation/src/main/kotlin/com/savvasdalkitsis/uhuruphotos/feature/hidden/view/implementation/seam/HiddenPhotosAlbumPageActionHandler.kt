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
package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.feature.hidden.domain.api.usecase.HiddenMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.HiddenMedia
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class HiddenPhotosAlbumPageActionHandler @Inject constructor(
    mediaUseCase: MediaUseCase,
    hiddenMediaUseCase: HiddenMediaUseCase,
    settingsUseCase: SettingsUseCase,
    biometricsUseCase: BiometricsUseCase
): ActionHandler<GalleryState, GalleryEffect, GalleryAction, GalleryMutation> by GalleryActionHandler(
    galleryRefresher = { mediaUseCase.refreshFavouriteMedia() },
    initialCollageDisplay = { hiddenMediaUseCase.getHiddenMediaGalleryDisplay() },
    collageDisplayPersistence = { _, galleryDisplay ->
        hiddenMediaUseCase.setHiddenMediaGalleryDisplay(galleryDisplay)
    },
    galleryDetailsEmptyCheck = {
        mediaUseCase.getHiddenMedia().getOrDefault(emptyList()).isEmpty()
    },
    galleryDetailsFlow = { _, effect ->
        settingsUseCase.observeBiometricsRequiredForHiddenPhotosAccess()
            .flatMapLatest { biometricsRequired ->
                val proceed = when {
                    biometricsRequired -> biometricsUseCase.authenticate(
                        string.authenticate,
                        string.authenticate_for_access_to_hidden,
                        string.authenticate_for_access_to_hidden_description,
                        true,
                    )
                    else -> Result.success(Unit)
                }
                if (proceed.isFailure) {
                    flow {
                        effect(NavigateBack)
                    }
                } else {
                    mediaUseCase.observeHiddenMedia()
                        .mapNotNull { it.getOrNull() }
                        .map { photoEntries ->
                            GalleryDetails(
                                title = Title.Resource(string.hidden_photos),
                                albums = listOf(
                                    Album(
                                        id = "hidden",
                                        displayTitle = "",
                                        location = null,
                                        photos = photoEntries,
                                    )
                                ),
                            )
                        }
                }
            }

    },
    lightboxSequenceDataSource = { HiddenMedia },
)