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
import com.github.michaelbull.result.onSuccess
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.extensions.isVideo
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.broadcast.CancelPrecacheWorkBroadcastReceiver
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.implementation.repository.FeedRepository
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaOperationResult.CHANGED
import com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.api.usecase.RemoteMediaPrecacher
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
internal class PrecacheFeedThumbnailsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val feedRepository: FeedRepository,
    private val remoteMediaPrecacher: RemoteMediaPrecacher,
    private val serverUseCase: ServerUseCase,
    foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker<CancelPrecacheWorkBroadcastReceiver>(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = R.string.precaching_thumbnails,
    notificationId = NOTIFICATION_ID,
    cancelBroadcastReceiver = CancelPrecacheWorkBroadcastReceiver::class.java,
) {

    override suspend fun work(): Result {
        updateProgress(0)
        val serverUrl = serverUseCase.getServerUrl()!!
        val mediaItemIds = feedRepository.getRemoteMediaCollectionsByDate(
            FeedFetchType.ALL
        )
            .items.entries.flatMap { entry ->
                entry.value.mapNotNull { collections ->
                    collections.photoId?.let { id ->
                        MediaId.Remote(id, collections.isVideo)
                    }
                }
            }
        for ((index, id) in mediaItemIds.withIndex()) {
            if (isStopped)
                break
            remoteMediaPrecacher.precacheMedia(id.thumbnailUri(serverUrl), id.isVideo)
                .onSuccess {
                    if (it == CHANGED) {
                        delay(100)
                    }
                }
            updateProgress(index, mediaItemIds.size)
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "precacheThumbnails"
        private const val NOTIFICATION_ID = 1281
    }
}