package com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface FeedWorkScheduler {

    fun scheduleFeedRefreshNow(shallow: Boolean)
    fun scheduleFeedRefreshPeriodic(
        existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy
    )
    fun observeFeedRefreshJob(): Flow<RefreshJobState>
    fun observeFeedRefreshJobStatus(): Flow<WorkInfo.State>
}