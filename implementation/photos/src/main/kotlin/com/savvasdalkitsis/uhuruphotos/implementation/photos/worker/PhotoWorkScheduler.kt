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
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.api.worker.WorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.worker.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhotoWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler,
    private val workerStatusUseCase: WorkerStatusUseCase,
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

    fun schedulePhotoOriginalFileRetrieve(id: String, video: Boolean) =
        workScheduler.scheduleNow<PhotoOriginalFileRetrieveWorker>(
            workName = PhotoOriginalFileRetrieveWorker.workName(id),
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = 2,
            backoffTimeUnit = TimeUnit.SECONDS,
        ) {
            putString(PhotoOriginalFileRetrieveWorker.KEY_ID, id)
            putBoolean(PhotoOriginalFileRetrieveWorker.KEY_VIDEO, video)
        }

    fun observePhotoOriginalFileRetrieveJobStatus(id: String): Flow<WorkInfo.State> =
        workerStatusUseCase.monitorUniqueJobStatus(PhotoOriginalFileRetrieveWorker.workName(id))

    fun schedulePhotoTrashing(id: String) {
        workScheduler.scheduleNow<PhotoTrashWorker>(
            workName = PhotoTrashWorker.trashPhotoWorkName(id)
        ) {
            putString(PhotoTrashWorker.KEY_ID, id)
        }
    }

    fun schedulePhotoRestoration(id: String) {
        workScheduler.scheduleNow<PhotoRestoreWorker>(
            workName = PhotoRestoreWorker.restorePhotoWorkName(id)
        ) {
            putString(PhotoRestoreWorker.KEY_ID, id)
        }
    }

    fun schedulePhotoDeletion(id: String) {
        workScheduler.scheduleNow<PhotoDeletionWorker>(
            workName = PhotoDeletionWorker.deletePhotoWorkName(id)
        ) {
            putString(PhotoDeletionWorker.KEY_ID, id)
        }
    }
}