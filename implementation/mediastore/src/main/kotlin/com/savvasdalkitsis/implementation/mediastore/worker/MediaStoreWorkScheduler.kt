package com.savvasdalkitsis.implementation.mediastore.worker

import androidx.work.ExistingWorkPolicy
import com.savvasdalkitsis.uhuruphotos.api.mediastore.worker.MediaStoreWorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.worker.WorkScheduler
import javax.inject.Inject

class MediaStoreWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler,
): MediaStoreWorkScheduler {

    override fun scheduleMediaStoreSyncNowIfNotRunning() {
        workScheduler.scheduleNow<MediaStoreSyncWorker>(
            workName = MediaStoreSyncWorker.WORK_NAME,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
        )
    }
}