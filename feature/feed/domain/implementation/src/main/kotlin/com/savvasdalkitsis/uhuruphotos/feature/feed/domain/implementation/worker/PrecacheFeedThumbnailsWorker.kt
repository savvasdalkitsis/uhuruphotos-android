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
package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.NotificationChannels
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.math.toProgressPercent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
internal class PrecacheFeedThumbnailsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val feedRepository: FeedRepository,
    private val mediaUseCase: RemoteMediaUseCase,
    private val foregroundInfoBuilder: ForegroundInfoBuilder,
) : CoroutineWorker(context, params) {

    override suspend fun doWork() = withContext(Dispatchers.IO) {
        val mediaCollections = feedRepository.getRemoteMediaCollectionsByDate()
            .items.entries.flatMap { it.value }
        for ((index, entry) in mediaCollections.withIndex()) {
            if (isStopped)
                break
            mediaUseCase.downloadThumbnail(entry.id, entry.isVideo)
            val progress = index.toProgressPercent(mediaCollections.size)
            setProgress(workDataOf(Progress to progress))
            setForegroundAsync(createForegroundInfo(progress))
        }
        Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo = createForegroundInfo(null)

    private fun createForegroundInfo(progress: Int?) = foregroundInfoBuilder.build(
        applicationContext,
        R.string.precaching_thumbnails,
        NOTIFICATION_ID,
        NotificationChannels.JOBS_CHANNEL_ID,
        progress,
    )
    companion object {
        const val Progress = "Progress"
        const val WORK_NAME = "precacheThumbnails"
        private const val NOTIFICATION_ID = 1281
    }
}