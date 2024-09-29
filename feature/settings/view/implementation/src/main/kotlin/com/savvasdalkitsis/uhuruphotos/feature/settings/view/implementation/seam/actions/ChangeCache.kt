/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.LIGHTBOX_PHOTO_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.LIGHTBOX_PHOTO_MEMORY
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.THUMBNAIL_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.THUMBNAIL_MEMORY
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.model.CacheType.VIDEO_DISK
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import kotlinx.coroutines.flow.flow

data class ChangeCache(
    val cacheType: CacheType,
    val sizeInMb: Float,
) : SettingsAction() {
    override fun SettingsActionsContext.handle(
        state: SettingsState
    ) = flow<SettingsMutation> {
        when(cacheType) {
            LIGHTBOX_PHOTO_MEMORY -> settingsUseCase.setLightboxPhotoMemCacheMaxLimit(sizeInMb.toInt())
            LIGHTBOX_PHOTO_DISK -> settingsUseCase.setLightboxPhotoDiskCacheMaxLimit(sizeInMb.toInt())
            THUMBNAIL_MEMORY -> settingsUseCase.setThumbnailMemCacheMaxLimit(sizeInMb.toInt())
            THUMBNAIL_DISK -> settingsUseCase.setThumbnailDiskCacheMaxLimit(sizeInMb.toInt())
            VIDEO_DISK -> settingsUseCase.setVideoDiskCacheMaxLimit(sizeInMb.toInt())
        }
    }
}
