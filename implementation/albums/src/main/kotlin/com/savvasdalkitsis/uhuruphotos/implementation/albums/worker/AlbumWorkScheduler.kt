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
package com.savvasdalkitsis.uhuruphotos.implementation.albums.worker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.api.albums.worker.AlbumWorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.albums.worker.RefreshJobState
import com.savvasdalkitsis.uhuruphotos.api.settings.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.api.worker.WorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.worker.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class AlbumWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler,
    private val workerStatusUseCase: WorkerStatusUseCase,
    private val settingsUseCase: SettingsUseCase,
) : AlbumWorkScheduler {

    override fun scheduleAlbumsRefreshNow(shallow: Boolean) =
        workScheduler.scheduleNow<AlbumDownloadWorker>(AlbumDownloadWorker.WORK_NAME) {
            putBoolean(AlbumDownloadWorker.KEY_SHALLOW, shallow)
        }

    override fun scheduleAlbumsRefreshPeriodic(
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy
    ) {
        if (settingsUseCase.getShouldPerformPeriodicFullSync()) {
            workScheduler.schedulePeriodic<AlbumDownloadWorker>(
                AlbumDownloadWorker.WORK_NAME,
                repeatInterval = settingsUseCase.getFeedSyncFrequency().toLong(),
                repeatIntervalTimeUnit = TimeUnit.HOURS,
                initialDelayDuration = 1,
                initialDelayTimeUnit = TimeUnit.HOURS,
                existingPeriodicWorkPolicy = existingPeriodicWorkPolicy,
                networkRequirement = settingsUseCase.getFullSyncNetworkRequirements(),
                requiresCharging = settingsUseCase.getFullSyncRequiresCharging(),
            )
        } else {
            workScheduler.workManager.cancelUniqueWork(AlbumDownloadWorker.WORK_NAME)
        }
    }

    override fun observeAlbumRefreshJob(): Flow<RefreshJobState> =
        workerStatusUseCase.monitorUniqueJob(AlbumDownloadWorker.WORK_NAME).map {
            RefreshJobState(
                status = it.state,
                progress = it.progress.getInt(AlbumDownloadWorker.Progress, 0)
            )
        }

    override fun observeAlbumRefreshJobStatus(): Flow<WorkInfo.State> =
        workerStatusUseCase.monitorUniqueJobStatus(AlbumDownloadWorker.WORK_NAME)
}