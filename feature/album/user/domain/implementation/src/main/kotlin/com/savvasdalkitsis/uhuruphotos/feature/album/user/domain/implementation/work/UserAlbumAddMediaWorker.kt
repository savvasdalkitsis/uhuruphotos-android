/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.mapBoth
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.repository.UserAlbumRepository
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundInfoBuilder
import com.savvasdalkitsis.uhuruphotos.foundation.notification.api.ForegroundNotificationWorker
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
internal class UserAlbumAddMediaWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val userAlbumRepository: UserAlbumRepository,
    foregroundInfoBuilder: ForegroundInfoBuilder,
) : ForegroundNotificationWorker<Nothing>(
    context,
    params,
    foregroundInfoBuilder,
    notificationTitle = string.adding_to_album,
    notificationId = NOTIFICATION_ID,
) {

    override suspend fun work(): Result = coroutineBinding {
        val albumId = params.inputData.getInt(KEY_ALBUM_ID, -1)
        val mediaIds = userAlbumRepository.getMediaAdditionQueue(albumId).bind()
        mediaIds.windowed(size = 10, step = 10, partialWindows = true).forEach { partialIds ->
            userAlbumRepository.addMediaToUserAlbum(albumId, partialIds).bind()
            userAlbumRepository.removeMediaFromAdditionQueue(albumId, partialIds)
        }
        userAlbumRepository.getMediaAdditionQueue(albumId).bind()
    }.mapBoth(
        success = { remaining ->
            if (remaining.isNotEmpty()) {
                failOrRetry()
            } else {
                Result.success()
            }
        },
        failure = { failOrRetry() },
    )

    private fun failOrRetry() = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        Result.failure()
    }

    companion object {
        const val KEY_ALBUM_ID = "albumId"
        fun workName(id: Int) = "addMediaToUserAlbum/$id"
        private const val NOTIFICATION_ID = 1290
    }
}
