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
internal class UserAlbumCreationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted private val params: WorkerParameters,
    private val userAlbumWorkScheduler: UserAlbumWorkScheduler,
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
        val name = params.inputData.getString(KEY_ALBUM_NAME)!!
        val addAfterCreation = params.inputData.getBoolean(KEY_ALBUM_ADD_AFTER_CREATION, false)
        val newAlbum = userAlbumRepository.createNewUserAlbum(name).bind()
        if (addAfterCreation) {
            userAlbumRepository.migrateQueueFromNewAlbum(name, newAlbum.id)
            userAlbumWorkScheduler.scheduleMediaAddition(newAlbum.id)
        }
    }.mapBoth(
        success = { Result.success() },
        failure = { failOrRetry() },
    )

    private fun failOrRetry() = if (params.runAttemptCount < 4) {
        Result.retry()
    } else {
        Result.failure()
    }

    companion object {
        const val KEY_ALBUM_NAME = "albumName"
        const val KEY_ALBUM_ADD_AFTER_CREATION = "addAfterCreation"
        fun workName(name: String) = "addMediaToUserAlbum/$name"
        private const val NOTIFICATION_ID = 1291
    }
}
