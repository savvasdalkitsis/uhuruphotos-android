package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.worker

import androidx.work.ExistingWorkPolicy
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.WorkScheduler
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