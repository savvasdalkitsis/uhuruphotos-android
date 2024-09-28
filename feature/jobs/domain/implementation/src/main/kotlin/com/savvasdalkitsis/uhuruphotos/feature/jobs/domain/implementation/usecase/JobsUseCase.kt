/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.implementation.usecase

import androidx.work.WorkInfo.State.BLOCKED
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.worker.FeedWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel.FEED_DETAILS_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel.FULL_FEED_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel.LOCAL_MEDIA_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel.PRECACHE_THUMBNAILS
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.BlockedModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.FailedModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.IdleModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.InProgressModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.QueuedModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobsStatusModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.usecase.JobsUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.usecase.LocalMediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.worker.LocalMediaWorkScheduler
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUIUseCase
import com.savvasdalkitsis.uhuruphotos.feature.welcome.domain.api.usecase.WelcomeUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.worker.api.model.RefreshJobState
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject

@AutoBind
@ActivityRetainedScoped
class JobsUseCase @Inject constructor(
    private val feedUseCase: FeedUseCase,
    private val feedWorkScheduler: FeedWorkScheduler,
    private val localMediaUseCase: LocalMediaUseCase,
    private val localMediaWorkScheduler: LocalMediaWorkScheduler,
    private val settingsUIUseCase: SettingsUIUseCase,
    private val welcomeUseCase: WelcomeUseCase,
) : JobsUseCase {

    override fun observeJobsStatus(): Flow<JobsStatusModel> =
        combine(
            welcomeUseCase.observeWelcomeStatus(),
            feedWorkScheduler.observeFeedRefreshJob(),
            feedWorkScheduler.observePrecacheThumbnailsJob(),
            localMediaUseCase.observeLocalMediaSyncJob(),
            feedWorkScheduler.observeFeedDetailsRefreshJob(),
        ) { welcomeStatus, feedRefresh, precacheThumbnails, localMedia, feedDetails ->
            JobsStatusModel(
                when {
                    welcomeStatus.hasRemoteAccess -> mapOf(
                        FULL_FEED_SYNC to feedRefresh.jobStatus(blockedBy = precacheThumbnails),
                        PRECACHE_THUMBNAILS to precacheThumbnails.jobStatus(blockedBy = feedRefresh),
                        LOCAL_MEDIA_SYNC to localMedia.jobStatus(),
                        FEED_DETAILS_SYNC to feedDetails.jobStatus(),
                    )
                    else -> mapOf(
                        LOCAL_MEDIA_SYNC to localMedia.jobStatus(),
                    )
                }
            )
        }

    override fun observeJobsStatusFilteredBySettings(): Flow<JobsStatusModel> = combine(
        observeJobsStatus(),
        settingsUIUseCase.observeShouldShowFeedSyncProgress(),
        settingsUIUseCase.observeShouldShowPrecacheProgress(),
        settingsUIUseCase.observeShouldShowLocalSyncProgress(),
        settingsUIUseCase.observeShouldShowFeedDetailsSyncProgress(),
    ) { jobs, showFeedProgress, showPrecacheProgress, showLocalProgress, showFeedDetailsProgress ->
        jobs.copy(jobs = jobs.jobs.mapValues { (job, status) ->
            status.unless(when (job) {
                FULL_FEED_SYNC -> showFeedProgress
                PRECACHE_THUMBNAILS  -> showPrecacheProgress
                LOCAL_MEDIA_SYNC -> showLocalProgress
                FEED_DETAILS_SYNC -> showFeedDetailsProgress
            })
        })
    }

    private fun JobStatusModel.unless(predicate: Boolean): JobStatusModel = if (predicate) this else IdleModel

    override fun startJob(job: JobModel) {
        when (job) {
            FULL_FEED_SYNC -> {
                feedUseCase.invalidateRemoteFeed()
                feedUseCase.scheduleFeedRefreshNow()
            }
            PRECACHE_THUMBNAILS -> feedWorkScheduler.schedulePrecacheThumbnailsNow()
            LOCAL_MEDIA_SYNC -> localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
            FEED_DETAILS_SYNC -> feedWorkScheduler.scheduleFeedDetailsRefreshNow()
        }
    }

    override fun cancelJob(job: JobModel) {
        when (job) {
            FULL_FEED_SYNC -> feedWorkScheduler.cancelFeedSync()
            PRECACHE_THUMBNAILS -> feedWorkScheduler.cancelPrecacheThumbnails()
            LOCAL_MEDIA_SYNC -> localMediaWorkScheduler.cancelLocalMediaSync()
            FEED_DETAILS_SYNC -> feedWorkScheduler.cancelFeedDetailsSync()
        }
    }

    private fun RefreshJobState?.jobStatus(
        blockedBy: RefreshJobState? = null
    ) = when {
        this?.status == RUNNING -> InProgressModel(
            progress
        )
        blockedBy?.status == RUNNING -> BlockedModel
        this?.status == FAILED -> FailedModel
        this?.status == BLOCKED || this?.status == ENQUEUED -> QueuedModel
        else -> IdleModel
    }
}
