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

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedImmediateWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker.FeedDownloadWorker
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker.PrecacheFeedThumbnailsWorker
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleNowNotificationUseCase
import javax.inject.Inject

internal class FeedImmediateWorkScheduler @Inject constructor(
    private val workScheduleNowNotificationUseCase: WorkScheduleNowNotificationUseCase,
) : FeedImmediateWorkScheduler {

    override fun scheduleFeedRefreshNow(shallow: Boolean) =
        workScheduleNowNotificationUseCase.scheduleNow(
            FeedDownloadWorker.WORK_NAME,
            FeedDownloadWorker::class,
        ) {
            putBoolean(FeedDownloadWorker.KEY_SHALLOW, shallow)
        }

    override fun schedulePrecacheThumbnailsNow() {
        workScheduleNowNotificationUseCase.scheduleNow(
            PrecacheFeedThumbnailsWorker.WORK_NAME,
            PrecacheFeedThumbnailsWorker::class,
        )
    }
}