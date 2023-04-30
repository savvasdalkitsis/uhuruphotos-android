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
package com.savvasdalkitsis.uhuruphotos.feature.media.remote.domain.implementation.worker

import androidx.work.BackoffPolicy
import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkScheduleUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoteMediaItemWorkScheduler @Inject constructor(
    private val workScheduleUseCase: WorkScheduleUseCase,
    private val workerStatusUseCase: WorkerStatusUseCase,
) {

    fun scheduleMediaItemFavourite(id: String, favourite: Boolean) =
        workScheduleUseCase.scheduleNow(
            workName = RemoteMediaItemFavouriteWorker.workName(id),
            RemoteMediaItemFavouriteWorker::class,
        ) {
            putString(RemoteMediaItemFavouriteWorker.KEY_ID, id)
            putBoolean(RemoteMediaItemFavouriteWorker.KEY_FAVOURITE, favourite)
        }

    fun scheduleMediaItemDetailsRetrieve(id: String) =
        workScheduleUseCase.scheduleNow(
            workName = RemoteMediaItemDetailsRetrieveWorker.workName(id),
            klass = RemoteMediaItemDetailsRetrieveWorker::class,
            backoffPolicy = BackoffPolicy.LINEAR,
            backoffDelay = 2,
            backoffTimeUnit = TimeUnit.SECONDS,
        ) {
            putString(RemoteMediaItemDetailsRetrieveWorker.KEY_ID, id)
        }

    fun scheduleMediaItemOriginalFileRetrieve(id: String, video: Boolean) =
        workScheduleUseCase.scheduleNow(
            workName = RemoteMediaItemOriginalFileRetrieveWorker.workName(id),
            klass = RemoteMediaItemOriginalFileRetrieveWorker::class,
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = 2,
            backoffTimeUnit = TimeUnit.SECONDS,
        ) {
            putString(RemoteMediaItemOriginalFileRetrieveWorker.KEY_ID, id)
            putBoolean(RemoteMediaItemOriginalFileRetrieveWorker.KEY_VIDEO, video)
        }

    fun observeMediaItemOriginalFileRetrieveJobStatus(id: String): Flow<WorkInfo.State?> =
        workerStatusUseCase.monitorUniqueJobStatus(RemoteMediaItemOriginalFileRetrieveWorker.workName(id))

    fun scheduleMediaItemTrashing(id: String) {
        workScheduleUseCase.scheduleNow(
            workName = RemoteMediaItemTrashWorker.workName(id),
            klass = RemoteMediaItemTrashWorker::class,
        ) {
            putString(RemoteMediaItemTrashWorker.KEY_ID, id)
        }
    }

    fun scheduleMediaItemRestoration(id: String) {
        workScheduleUseCase.scheduleNow(
            workName = RemoteMediaItemRestoreWorker.workName(id),
            klass = RemoteMediaItemRestoreWorker::class,
        ) {
            putString(RemoteMediaItemRestoreWorker.KEY_ID, id)
        }
    }

    fun scheduleMediaItemDeletion(id: String) {
        workScheduleUseCase.scheduleNow(
            workName = RemoteMediaItemDeletionWorker.workName(id),
            klass = RemoteMediaItemDeletionWorker::class,
        ) {
            putString(RemoteMediaItemDeletionWorker.KEY_ID, id)
        }
    }
}