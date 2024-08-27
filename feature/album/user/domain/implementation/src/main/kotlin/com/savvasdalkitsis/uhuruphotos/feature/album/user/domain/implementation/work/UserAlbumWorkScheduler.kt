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

import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.work.UserAlbumAddMediaWorker.Companion.KEY_ALBUM_ID
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.work.UserAlbumCreationWorker.Companion.KEY_ALBUM_ADD_AFTER_CREATION
import com.savvasdalkitsis.uhuruphotos.feature.album.user.domain.implementation.work.UserAlbumCreationWorker.Companion.KEY_ALBUM_NAME
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import javax.inject.Inject

class UserAlbumWorkScheduler @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase,
) {
    fun scheduleMediaAddition(albumId: Int) {
        workScheduleUseCase.scheduleNow(
            workName = UserAlbumAddMediaWorker.workName(albumId),
            klass = UserAlbumAddMediaWorker::class,
        ) {
            putInt(KEY_ALBUM_ID, albumId)
        }
    }

    fun scheduleNewAlbumCreation(name: String, performAdditionAfterCreation: Boolean = false) {
        workScheduleUseCase.scheduleNow(
            workName = UserAlbumCreationWorker.workName(name),
            klass = UserAlbumCreationWorker::class,
        ) {
            putString(KEY_ALBUM_NAME, name)
            putBoolean(KEY_ALBUM_ADD_AFTER_CREATION, performAdditionAfterCreation)
        }
    }
}