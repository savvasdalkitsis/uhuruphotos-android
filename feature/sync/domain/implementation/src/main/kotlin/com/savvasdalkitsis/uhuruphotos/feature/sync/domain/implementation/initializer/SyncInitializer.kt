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
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.usecase.SyncUseCase
import com.savvasdalkitsis.uhuruphotos.feature.upload.domain.api.usecase.UploadUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.runningFold
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
            uploadSchedules().collectLatest(::scheduleUploads)
        }
    }

    private suspend fun scheduleUploads(schedule: UploadSchedule) {
        uploadUseCase.scheduleUploads(
            networkType = schedule.networkType,
            requiresCharging = schedule.requiresCharging,
            keepExisting = schedule.keepExisting,
        )
    }

    internal fun uploadSchedules(): Flow<UploadSchedule> = combine(
        syncUseCase.observePendingItems(),
        settingsUseCase.observeCloudSyncNetworkRequirements(),
        settingsUseCase.observeCloudSyncRequiresCharging(),
    ) { pendingItems, networkRequirement, requiresCharging ->
        PendingUploads(pendingItems.map { it.id }.toSet(), networkRequirement, requiresCharging)
    }.distinctUntilChanged()
        .runningFold(null as Pair<PendingUploads?, PendingUploads>?) { previous, current ->
            previous?.second to current
        }
        .filterNotNull()
        .mapNotNull { (previous, current) -> current.scheduleAfter(previous) }

    internal data class UploadSchedule(
        val networkType: NetworkType,
        val requiresCharging: Boolean,
        val keepExisting: Boolean,
    )

    private data class PendingUploads(
        val ids: Set<Long>,
        val networkType: NetworkType,
        val requiresCharging: Boolean,
    ) {
        fun scheduleAfter(previous: PendingUploads?): UploadSchedule? {
            if (ids.isEmpty()) {
                return null
            }
            if (previous == null || previous.ids.isEmpty() || !sameRequirementsAs(previous)) {
                return schedule(keepExisting = false)
            }
            return schedule(keepExisting = true).takeUnless { previous.ids.containsAll(ids) }
        }

        private fun sameRequirementsAs(other: PendingUploads) =
            networkType == other.networkType && requiresCharging == other.requiresCharging

        private fun schedule(keepExisting: Boolean) = UploadSchedule(networkType, requiresCharging, keepExisting)
    }
}
