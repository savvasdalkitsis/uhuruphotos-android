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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.worker

import androidx.work.BackoffPolicy
import com.savvasdalkitsis.uhuruphotos.api.worker.WorkScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhotoWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler
) {

    fun schedulePhotoFavourite(id: String, favourite: Boolean) =
        workScheduler.scheduleNow<PhotoFavouriteWorker>(
            workName = PhotoFavouriteWorker.workName(id)
        ) {
            putString(PhotoFavouriteWorker.KEY_ID, id)
            putBoolean(PhotoFavouriteWorker.KEY_FAVOURITE, favourite)
        }

    fun schedulePhotoDetailsRetrieve(id: String) =
        workScheduler.scheduleNow<PhotoDetailsRetrieveWorker>(
            workName = PhotoDetailsRetrieveWorker.workName(id),
            backoffPolicy = BackoffPolicy.LINEAR,
            backoffDelay = 2,
            backoffTimeUnit = TimeUnit.SECONDS,
        ) {
            putString(PhotoDetailsRetrieveWorker.KEY_ID, id)
        }

    fun schedulePhotoDeletion(id: String) {
        workScheduler.scheduleNow<PhotoDeletionWorker>(
            workName = PhotoDeletionWorker.workName(id)
        ) {
            putString(PhotoDeletionWorker.KEY_ID, id)
        }
    }
}