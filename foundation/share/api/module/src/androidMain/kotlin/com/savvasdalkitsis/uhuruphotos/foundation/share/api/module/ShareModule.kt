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
package com.savvasdalkitsis.uhuruphotos.foundation.share.api.module

import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.module.ImageModule
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.module.cache.ImageCacheModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase

object ShareModule {

    val shareUseCase: ShareUseCase
        get() = com.savvasdalkitsis.uhuruphotos.foundation.share.implementation.usecase.ShareUseCase(
            ImageCacheModule.fullImageDiskCache,
            NavigationModule.navigator,
            ImageModule.fullImageLoader,
            SettingsModule.settingsUseCase,
            AndroidModule.applicationContext,
        )

}