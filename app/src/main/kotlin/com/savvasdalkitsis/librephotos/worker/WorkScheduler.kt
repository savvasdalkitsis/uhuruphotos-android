package com.savvasdalkitsis.librephotos.worker

import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkScheduler @Inject constructor(
    val workManager: WorkManager,
) {
    inline fun <reified W: CoroutineWorker> scheduleNow(
        workName: String,
        params: Data.Builder.() -> Data.Builder = { this },
    ) {
        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<W>()
                .setInputData(params(Data.Builder()).build())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build(),
        )
    }

    inline fun <reified W: CoroutineWorker> schedulePeriodic(
        workName: String,
        repeatInterval: Long,
        repeatIntervalTimeUnit: TimeUnit,
        initialDelayDuration: Long,
        initialDelayTimeUnit: TimeUnit,
    ) {
        workManager.enqueueUniquePeriodicWork(
            workName,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<W>(repeatInterval, repeatIntervalTimeUnit)
                .setInitialDelay(initialDelayDuration, initialDelayTimeUnit)
                .build(),
        )
    }
}