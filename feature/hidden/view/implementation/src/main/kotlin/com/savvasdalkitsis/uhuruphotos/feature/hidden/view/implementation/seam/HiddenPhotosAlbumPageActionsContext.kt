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

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getOr
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryActionsContextFactory
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.hidden.domain.api.usecase.HiddenMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.HiddenMedia
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.usecase.BiometricsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

internal class HiddenPhotosAlbumPageActionsContext @Inject constructor(
    mediaUseCase: MediaUseCase,
    hiddenMediaUseCase: HiddenMediaUseCase,
    settingsUseCase: SettingsUseCase,
    biometricsUseCase: BiometricsUseCase,
    navigator: Navigator,
    galleryActionsContextFactory: GalleryActionsContextFactory,
) {
    val galleryActionsContext = galleryActionsContextFactory.create(
        galleryRefresher = { mediaUseCase.refreshHiddenMedia() },
        initialCollageDisplay = { hiddenMediaUseCase.getHiddenMediaGalleryDisplay() },
        collageDisplayPersistence = { _, galleryDisplay ->
            hiddenMediaUseCase.setHiddenMediaGalleryDisplay(galleryDisplay)
        },
        shouldRefreshOnLoad = { true },
        galleryDetailsFlow = { _ ->
            settingsUseCase.observeBiometricsRequiredForHiddenPhotosAccess()
                .flatMapLatest { biometricsRequired ->
                    val proceed = when {
                        biometricsRequired -> biometricsUseCase.authenticate(
                            string.authenticate,
                            string.authenticate_for_access_to_hidden,
                            string.authenticate_for_access_to_hidden_description,
                            true,
                        )

                        else -> Ok(Unit)
                    }
                    if (proceed.isErr) {
                        flow {
                            navigator.navigateUp()
                        }
                    } else {
                        mediaUseCase.observeHiddenMedia()
                            .mapNotNull { it.getOr(null) }
                            .map { photoEntries ->
                                GalleryDetails(
                                    title = Title.Resource(string.hidden_media),
                                    clusters = listOf(
                                        Cluster(
                                            id = "hidden",
                                            displayTitle = "",
                                            location = null,
                                            cels = photoEntries.map {
                                                it.toCel()
                                            }.toPersistentList(),
                                        )
                                    ),
                                )
                            }
                    }
                }

        },
        lightboxSequenceDataSource = { HiddenMedia },
    )
}