package com.savvasdalkitsis.uhuruphotos.albums.api.worker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface AlbumWorkScheduler {

    fun scheduleAlbumsRefreshNow(shallow: Boolean)
    fun scheduleAlbumsRefreshPeriodic(
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy
    )
    fun observeAlbumRefreshJobStatus(): Flow<WorkInfo.State>
}