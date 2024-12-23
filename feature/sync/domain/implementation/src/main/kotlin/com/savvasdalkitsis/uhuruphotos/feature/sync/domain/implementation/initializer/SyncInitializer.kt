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
package com.savvasdalkitsis.uhuruphotos.feature.sync.domain.implementation.initializer

import android.app.Application
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@AutoBindIntoSet
class SyncInitializer @Inject constructor(
    private val syncUseCase: SyncUseCase,
    private val uploadUseCase: UploadUseCase,
    private val settingsUseCase: SettingsUseCase,
) : ApplicationCreated {
    override fun onAppCreated(app: Application) {
        MainScope().launch(Dispatchers.IO) {
            combine(
                syncUseCase.observePendingItems(),
                settingsUseCase.observeCloudSyncNetworkRequirements(),
                settingsUseCase.observeCloudSyncRequiresCharging(),
            ) { _, networkRequirement, requiresCharging ->
                networkRequirement to requiresCharging
            }.distinctUntilChanged().collectLatest { (networkRequirement, requiresCharging) ->
                uploadUseCase.scheduleUploads(
                    networkType = networkRequirement,
                    requiresCharging = requiresCharging,
                )
            }
        }
    }
}