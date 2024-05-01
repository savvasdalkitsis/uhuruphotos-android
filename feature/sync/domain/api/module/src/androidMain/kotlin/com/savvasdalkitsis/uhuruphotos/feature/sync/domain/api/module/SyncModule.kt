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
package com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.module

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.SettingsModule
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.implementation.initializer.SyncInitializer
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.module.UploadModule
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.module.UploadsModule
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.module.PreferencesModule

object SyncModule {

    val syncUseCase: SyncUseCase
        get() = com.savvasdalkitsis.uhuruphotos.feature.sync.domain.implementation.usecase.SyncUseCase(
            WelcomeModule.welcomeUseCase,
            PreferencesModule.plainTextPreferences,
            FeedModule.feedUseCase,
            UploadsModule.uploadsUseCase,
        )

    val syncInitializer: ApplicationCreated by singleInstance {
        SyncInitializer(
            syncUseCase,
            UploadModule.uploadUseCase,
            UploadsModule.uploadsUseCase,
            SettingsModule.settingsUseCase
        )
    }
}