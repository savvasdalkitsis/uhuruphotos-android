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
import androidx.work.WorkerParameters
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.broadcast.CancelPrecacheWorkBroadcastReceiver
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
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
    foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker<CancelPrecacheWorkBroadcastReceiver>(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = R.string.precaching_thumbnails,
    notificationId = NOTIFICATION_ID,
    cancelBroadcastReceiver = CancelPrecacheWorkBroadcastReceiver::class.java,
) {

    override suspend fun work() = withContext(Dispatchers.IO) {
        updateProgress(0)
        val mediaItems = feedRepository.getRemoteMediaCollectionsByDate()
            .items.entries.flatMap { it.value }
        for ((index, entry) in mediaItems.withIndex()) {
            if (isStopped)
                break
            entry.photoId?.let { mediaId ->
                mediaUseCase.downloadThumbnail(mediaId, entry.isVideo)
            }
            updateProgress(index, mediaItems.size)
        }
        Result.success()
    }

    companion object {
        const val WORK_NAME = "precacheThumbnails"
        private const val NOTIFICATION_ID = 1281
    }
}