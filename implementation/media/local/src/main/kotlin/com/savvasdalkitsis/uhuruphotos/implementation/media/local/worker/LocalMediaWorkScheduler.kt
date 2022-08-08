package com.savvasdalkitsis.uhuruphotos.implementation.media.local.worker

import androidx.work.ExistingWorkPolicy
import com.savvasdalkitsis.uhuruphotos.api.media.local.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.api.worker.WorkScheduler
import javax.inject.Inject

class LocalMediaWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler,
): LocalMediaWorkScheduler {

    override fun scheduleLocalMediaSyncNowIfNotRunning() {
        workScheduler.scheduleNow<LocalMediaSyncWorker>(
            workName = LocalMediaSyncWorker.WORK_NAME,
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
        )
    }
}