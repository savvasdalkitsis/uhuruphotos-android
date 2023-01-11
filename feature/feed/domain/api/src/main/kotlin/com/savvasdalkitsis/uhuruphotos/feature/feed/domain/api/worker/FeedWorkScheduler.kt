package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface FeedWorkScheduler {

    fun scheduleFeedRefreshNow(shallow: Boolean)
    fun scheduleFeedRefreshPeriodic(
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy
    )
    fun schedulePrecacheThumbnailsNow()
    fun observeFeedRefreshJob(): Flow<RefreshJobState?>
    fun observeFeedRefreshJobStatus(): Flow<WorkInfo.State?>
    fun cancelFullFeedSync()
    fun observePrecacheThumbnailsJob(): Flow<RefreshJobState?>
    fun observePrecacheThumbnailsJobStatus(): Flow<WorkInfo.State?>
    fun cancelPrecacheThumbnails()
}