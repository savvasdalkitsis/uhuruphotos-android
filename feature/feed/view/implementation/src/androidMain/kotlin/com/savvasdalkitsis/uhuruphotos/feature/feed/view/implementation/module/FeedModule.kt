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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.module

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.module.PlatformAuthModule
import com.savvasdalkitsis.uhuruphotos.feature.avatar.domain.api.module.AvatarModule
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.module.BatteryModule
import com.savvasdalkitsis.uhuruphotos.feature.download.domain.api.module.DownloadModule
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.module.FeedModule
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.SelectionList
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.module.JobsModule
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.module.CommonMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.memories.domain.api.module.MemoriesModule
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.module.ui.SettingsUiModule
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.module.SyncModule
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.module.UploadModule
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.module.WelcomeModule
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.module.NavigationModule
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.module.ShareModule
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.module.ToasterModule
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.module.UiModule

internal object FeedModule {

    val feedActionsContext get() = FeedActionsContext(
        AvatarModule.avatarUseCase,
        DownloadModule.downloadUseCase,
        UploadModule.uploadUseCase,
        FeedModule.feedUseCase,
        FeedModule.feedWorkScheduler,
        CommonMediaModule.mediaUseCase,
        JobsModule.jobsUseCase,
        LocalMediaModule.localMediaDeletionUseCase,
        selectionList,
        SettingsUiModule.settingsUiUseCase,
        MemoriesModule.memoriesUseCase,
        LocalMediaModule.localMediaWorkScheduler,
        UiModule.uiUseCase,
        ShareModule.shareUseCase,
        ToasterModule.toasterUseCase,
        NavigationModule.navigator,
        WelcomeModule.welcomeUseCase,
        SyncModule.syncUseCase,
        PlatformAuthModule.authenticationLoginUseCase,
        PlatformAuthModule.serverUseCase,
        BatteryModule.batteryUseCase,
    )

    private val selectionList get() = SelectionList()
}