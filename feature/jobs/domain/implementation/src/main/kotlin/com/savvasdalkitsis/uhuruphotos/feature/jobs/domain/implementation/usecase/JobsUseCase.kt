package com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.implementation.usecase

import androidx.work.WorkInfo.State.BLOCKED
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedImmediateWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.FEED_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.LOCAL_MEDIA_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.PRECACHE_THUMBNAILS
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Blocked
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Failed
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Idle
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.InProgress
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Queued
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobsStatus
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.usecase.JobsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.model.RefreshJobState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class JobsUseCase @Inject constructor(
    private val feedWorkScheduler: FeedWorkScheduler,
    private val feedImmediateWorkScheduler: FeedImmediateWorkScheduler,
    private val localMediaUseCase: LocalMediaUseCase,
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
) : JobsUseCase {

    override fun observeJobsStatus(): Flow<JobsStatus> =
        combine(
            feedWorkScheduler.observeFeedRefreshJob(),
            feedWorkScheduler.observePrecacheThumbnailsJob(),
            localMediaUseCase.observeLocalMediaSyncJob(),
        ) { feedRefresh, precacheThumbnails, localMedia ->
            JobsStatus(
                mapOf(
                    FEED_SYNC to feedRefresh.jobStatus(blockedBy = precacheThumbnails),
                    PRECACHE_THUMBNAILS to precacheThumbnails.jobStatus(blockedBy = feedRefresh),
                    LOCAL_MEDIA_SYNC to localMedia.jobStatus()
                )
            )
        }

    override fun startJob(job: Job) {
        when (job) {
            FEED_SYNC -> feedImmediateWorkScheduler.scheduleFeedRefreshNow(shallow = false)
            PRECACHE_THUMBNAILS -> feedImmediateWorkScheduler.schedulePrecacheThumbnailsNow()
            LOCAL_MEDIA_SYNC -> localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
        }
    }

    override fun cancelJob(job: Job) {
        when (job) {
            FEED_SYNC -> feedWorkScheduler.cancelFullFeedSync()
            PRECACHE_THUMBNAILS -> feedWorkScheduler.cancelPrecacheThumbnails()
            LOCAL_MEDIA_SYNC -> localMediaWorkScheduler.cancelLocalMediaSync()
        }
    }

    private fun RefreshJobState?.jobStatus(
        blockedBy: RefreshJobState? = null
    ) = when {
        this?.status == RUNNING -> InProgress(
            progress
        )
        blockedBy?.status == RUNNING -> Blocked
        this?.status == FAILED -> Failed
        this?.status == BLOCKED || this?.status == ENQUEUED -> Queued
        else -> Idle
    }
}