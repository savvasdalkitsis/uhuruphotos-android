package com.savvasdalkitsis.librephotos.albums.worker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import com.savvasdalkitsis.librephotos.albums.api.worker.AlbumWorkScheduler
import com.savvasdalkitsis.librephotos.worker.WorkScheduler
import com.savvasdalkitsis.librephotos.worker.usecase.WorkerStatusUseCase
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlbumWorkScheduler @Inject constructor(
    private val workScheduler: WorkScheduler,
    private val workerStatusUseCase: WorkerStatusUseCase,
) : AlbumWorkScheduler {

    override fun scheduleAlbumsRefreshNow(shallow: Boolean) =
        workScheduler.scheduleNow<AlbumDownloadWorker>(AlbumDownloadWorker.WORK_NAME) {
            putBoolean(AlbumDownloadWorker.KEY_SHALLOW, shallow)
        }

    override fun scheduleAlbumsRefreshPeriodic(
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

    override fun observeAlbumRefreshJobStatus(): Flow<WorkInfo.State> =
        workerStatusUseCase.monitorUniqueJobStatus(AlbumDownloadWorker.WORK_NAME)
}