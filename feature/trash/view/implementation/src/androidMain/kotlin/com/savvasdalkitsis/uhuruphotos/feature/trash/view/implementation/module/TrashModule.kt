/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.module.GalleryModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.trash.domain.api.module.TrashModule
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashAlbumPageActionsContext
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.module.BiometricsModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule

internal object TrashModule {

    val trashActionsContext get() = TrashActionsContext(
        SettingsModule.settingsUseCase,
        BiometricsModule.biometricsUseCase,
        NavigationModule.navigator,
    )

    val trashPageActionsContext get() = TrashAlbumPageActionsContext(
        TrashModule.trashUseCase,
        SettingsModule.settingsUseCase,
        BiometricsModule.biometricsUseCase,
        NavigationModule.navigator,
        GalleryModule.galleryActionsContextFactory,
    )
}