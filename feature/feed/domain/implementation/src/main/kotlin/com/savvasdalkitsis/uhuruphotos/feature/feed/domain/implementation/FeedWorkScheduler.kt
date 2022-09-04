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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.RefreshJobState
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker.FeedDownloadWorker
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.WorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class FeedWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler,
    private val workerStatusUseCase: WorkerStatusUseCase,
    private val settingsUseCase: SettingsUseCase
) : FeedWorkScheduler {

    override fun scheduleFeedRefreshNow(shallow: Boolean) =
        workScheduler.scheduleNow<FeedDownloadWorker>(FeedDownloadWorker.WORK_NAME) {
            putBoolean(FeedDownloadWorker.KEY_SHALLOW, shallow)
        }

    override fun scheduleFeedRefreshPeriodic(
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy
    ) {
        if (settingsUseCase.getShouldPerformPeriodicFullSync()) {
            workScheduler.schedulePeriodic<FeedDownloadWorker>(
                FeedDownloadWorker.WORK_NAME,
                repeatInterval = settingsUseCase.getFeedSyncFrequency().toLong(),
                repeatIntervalTimeUnit = TimeUnit.HOURS,
                initialDelayDuration = 1,
                initialDelayTimeUnit = TimeUnit.HOURS,
                existingPeriodicWorkPolicy = existingPeriodicWorkPolicy,
                networkRequirement = settingsUseCase.getFullSyncNetworkRequirements(),
                requiresCharging = settingsUseCase.getFullSyncRequiresCharging(),
            )
        } else {
            workScheduler.workManager.cancelUniqueWork(FeedDownloadWorker.WORK_NAME)
        }
    }

    override fun observeFeedRefreshJob(): Flow<RefreshJobState> =
        workerStatusUseCase.monitorUniqueJob(FeedDownloadWorker.WORK_NAME).map {
            RefreshJobState(
                status = it.state,
                progress = it.progress.getInt(FeedDownloadWorker.Progress, 0)
            )
        }

    override fun observeFeedRefreshJobStatus(): Flow<WorkInfo.State> =
        workerStatusUseCase.monitorUniqueJobStatus(FeedDownloadWorker.WORK_NAME)
}