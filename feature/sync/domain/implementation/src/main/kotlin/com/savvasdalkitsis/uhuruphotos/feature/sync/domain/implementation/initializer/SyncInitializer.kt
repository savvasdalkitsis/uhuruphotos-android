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
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.model.UploadItem
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.feature.uploads.domain.api.usecase.UploadsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import se.ansman.dagger.auto.AutoBindIntoSet
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@AutoBindIntoSet
class SyncInitializer @Inject constructor(
    private val syncUseCase: SyncUseCase,
    private val uploadUseCase: UploadUseCase,
    private val uploadsUseCase: UploadsUseCase,
    private val settingsUseCase: SettingsUseCase,
) : ApplicationCreated {
    override fun onAppCreated(app: Application) {
        GlobalScope.launch(Dispatchers.Default) {
            combine(
                syncUseCase.observePendingItems().distinctUntilChanged(),
                settingsUseCase.observeCloudSyncNetworkRequirements(),
                settingsUseCase.observeCloudSyncRequiresCharging(),
            ) { pending, networkRequirement, requiresCharging ->
                Triple(pending, networkRequirement, requiresCharging)
            }.collectLatest { (pending, networkRequirement, requiresCharging) ->
                uploadUseCase.scheduleUploads(
                    networkType = networkRequirement,
                    requiresCharging = requiresCharging,
                    items = pending.toTypedArray()
                )
            }
        }
        GlobalScope.launch(Dispatchers.Default) {
            combine(
                settingsUseCase.observeCloudSyncNetworkRequirements(),
                settingsUseCase.observeCloudSyncRequiresCharging(),
            ) { networkRequirement, requiresCharging ->
                networkRequirement to requiresCharging
            }.collectLatest { (networkRequirement, requiresCharging) ->
                val inFlight = uploadsUseCase.observeIndividualUploadsInFlight().map { uploads ->
                    uploads.jobs
                        .filter { !it.latestJobState.state.isFinished }
                        .map {
                            UploadItem(it.localItemId, it.contentUri)
                        }
                }.firstOrNull()
                if (inFlight != null) {
                    uploadUseCase.scheduleUpload(
                        networkType = networkRequirement,
                        requiresCharging = requiresCharging,
                        items = inFlight.toTypedArray()
                    )
                }
            }
        }
    }
}