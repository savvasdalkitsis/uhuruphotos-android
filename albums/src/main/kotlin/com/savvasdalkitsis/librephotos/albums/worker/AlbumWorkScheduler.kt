package com.savvasdalkitsis.librephotos.albums.worker

import androidx.work.ExistingPeriodicWorkPolicy
import com.savvasdalkitsis.librephotos.worker.WorkScheduler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlbumWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler,
) {

    fun scheduleAlbumsRefreshNow(shallow: Boolean) =
        workScheduler.scheduleNow<AlbumDownloadWorker>(AlbumDownloadWorker.WORK_NAME) {
            putBoolean(AlbumDownloadWorker.KEY_SHALLOW, shallow)
        }

    fun scheduleAlbumsRefreshPeriodic(
        hoursInterval: Int,
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy
    ) {
        workScheduler.schedulePeriodic<AlbumDownloadWorker>(
            AlbumDownloadWorker.WORK_NAME,
            repeatInterval = hoursInterval.toLong(),
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            initialDelayDuration = 1,
            initialDelayTimeUnit = TimeUnit.HOURS,
            existingPeriodicWorkPolicy = existingPeriodicWorkPolicy,
        )
    }
}