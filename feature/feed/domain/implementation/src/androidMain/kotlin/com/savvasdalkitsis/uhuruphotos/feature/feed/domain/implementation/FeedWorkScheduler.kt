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
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker.FeedDetailsDownloadWorker
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker.FeedDownloadWorker
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker.PrecacheFeedThumbnailsWorker
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.model.RefreshJobState
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.ansman.dagger.auto.AutoBind
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AutoBind
internal class FeedWorkScheduler @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase,
    private val workerStatusUseCase: WorkerStatusUseCase,
    private val settingsUseCase: SettingsUseCase
) : FeedWorkScheduler {

    override fun scheduleFeedRefreshPeriodic(
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy
    ) {
        if (settingsUseCase.getShouldPerformPeriodicFullSync()) {
            workScheduleUseCase.schedulePeriodic(
                FeedDownloadWorker.WORK_NAME,
                FeedDownloadWorker::class,
                repeatInterval = settingsUseCase.getFeedSyncFrequency().toLong(),
                repeatIntervalTimeUnit = TimeUnit.HOURS,
                initialDelayDuration = 1,
                initialDelayTimeUnit = TimeUnit.HOURS,
                existingPeriodicWorkPolicy = existingPeriodicWorkPolicy,
                networkRequirement = settingsUseCase.getFullSyncNetworkRequirements(),
                requiresCharging = settingsUseCase.getFullSyncRequiresCharging(),
            )
        } else {
            workScheduleUseCase.cancelUniqueWork(FeedDownloadWorker.WORK_NAME)
        }
    }

    override fun observeFeedRefreshJob(): Flow<RefreshJobState?> =
        workerStatusUseCase.monitorUniqueJob(FeedDownloadWorker.WORK_NAME).map {
            it?.let { work ->
                RefreshJobState(
                    status = work.state,
                    progress = ForegroundNotificationWorker.getProgressOf(work)
                )
            }
        }

    override fun cancelFullFeedSync() {
        workScheduleUseCase.cancelUniqueWork(FeedDownloadWorker.WORK_NAME)
    }

    override fun observeFeedRefreshJobStatus(): Flow<WorkInfo.State?> =
        workerStatusUseCase.monitorUniqueJobStatus(FeedDownloadWorker.WORK_NAME)

    override fun observePrecacheThumbnailsJob(): Flow<RefreshJobState?> =
        workerStatusUseCase.monitorUniqueJob(PrecacheFeedThumbnailsWorker.WORK_NAME).map {
            it?.let { work ->
                RefreshJobState(
                    status = work.state,
                    progress = ForegroundNotificationWorker.getProgressOf(work)
                )
            }
        }

    override fun cancelPrecacheThumbnails() {
        workScheduleUseCase.cancelUniqueWork(PrecacheFeedThumbnailsWorker.WORK_NAME)
    }

    override fun observePrecacheThumbnailsJobStatus(): Flow<WorkInfo.State?> =
        workerStatusUseCase.monitorUniqueJobStatus(PrecacheFeedThumbnailsWorker.WORK_NAME)

    override fun scheduleFeedRefreshNow(shallow: Boolean) =
        workScheduleUseCase.scheduleNow(
            FeedDownloadWorker.WORK_NAME,
            FeedDownloadWorker::class,
        ) {
            putBoolean(FeedDownloadWorker.KEY_SHALLOW, shallow)
        }

    override fun schedulePrecacheThumbnailsNow() {
        workScheduleUseCase.scheduleNow(
            PrecacheFeedThumbnailsWorker.WORK_NAME,
            PrecacheFeedThumbnailsWorker::class,
        )
    }

    override fun scheduleFeedDetailsRefreshNow() {
        workScheduleUseCase.scheduleNow(
            FeedDetailsDownloadWorker.WORK_NAME,
            FeedDetailsDownloadWorker::class,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
            networkRequirement = NetworkType.CONNECTED,
        )
    }

    override fun observeFeedDetailsRefreshJob(): Flow<RefreshJobState?> =
        workerStatusUseCase.monitorUniqueJob(FeedDetailsDownloadWorker.WORK_NAME).map {
            it?.let { work ->
                RefreshJobState(
                    status = work.state,
                    progress = ForegroundNotificationWorker.getProgressOf(work)
                )
            }
        }

    override fun cancelFeedDetailsSync() {
        workScheduleUseCase.cancelUniqueWork(FeedDetailsDownloadWorker.WORK_NAME)
    }
}