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
package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.module.GalleryModule
import com.savvasdalkitsis.uhuruphotos.feature.hidden.domain.api.module.HiddenModule
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAlbumPageActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.foundation.biometrics.api.module.BiometricsModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule

internal object HiddenModule {

    val hiddenPhotosActionsContext get() = HiddenPhotosActionsContext(
        SettingsModule.settingsUseCase,
        BiometricsModule.biometricsUseCase,
        NavigationModule.navigator,
    )

    val hiddenPhotosAlbumPageActionsContext get() = HiddenPhotosAlbumPageActionsContext(
        CommonMediaModule.mediaUseCase,
        HiddenModule.hiddenMediaUseCase,
        SettingsModule.settingsUseCase,
        BiometricsModule.biometricsUseCase,
        NavigationModule.navigator,
        GalleryModule.galleryActionsContextFactory,
    )
}