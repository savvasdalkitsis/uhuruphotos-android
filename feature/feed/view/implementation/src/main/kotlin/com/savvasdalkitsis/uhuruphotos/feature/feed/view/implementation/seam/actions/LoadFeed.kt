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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.HideLocalStoragePermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.HideMemories
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowCloudSyncRequest
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowClusters
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowLocalMediaSyncRunning
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowLocalStoragePermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowLostServerConnection
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowNoPhotosFound
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

data object LoadFeed : FeedAction() {

    context(FeedActionsContext) override fun handle(
        state: FeedState
    ) = merge(
        showLibraryTab(),
        changeDisplay(),
        flowOf(Loading),
        showClusters(),
        localMediaPermissionHeader(),
        cloudSyncHeader(),
        localMediaSyncStatus(),
        memories(),
        changeItemSyncDisplay(),
        setAutoHideNav(),
        lostServerConnection(),
    )

    private fun FeedActionsContext.lostServerConnection() =
        welcomeUseCase.observeWelcomeStatus().map {
            ShowLostServerConnection(it.hasLostRemoteAccess)
        }

    private fun FeedActionsContext.memories() =
        settingsUseCase.observeMemoriesEnabled().flatMapLatest { enabled ->
            if (enabled) {
                memoriesUseCase.observeMemories().map { memoryCollections ->
                    memoryCollections.map { (collection, yearsAgo) ->
                        MemoryCel(
                            yearsAgo = yearsAgo,
                            cels = collection.mediaItems.map { it.toCel() }.toPersistentList(),
                        )
                    }
                }.map(FeedMutation::ShowMemories)
            } else {
                flowOf(HideMemories)
            }
        }

    private fun FeedActionsContext.localMediaSyncStatus() =
        jobsUseCase.observeJobsStatusFilteredBySettings().map { it.jobs[Job.LOCAL_MEDIA_SYNC] }.map {
            ShowLocalMediaSyncRunning(it == JobStatus.Queued || it is JobStatus.InProgress)
        }

    private fun FeedActionsContext.localMediaPermissionHeader() =
        mediaUseCase.observeLocalMedia()
            .mapNotNull {
                when (it) {
                    is MediaItemsOnDevice.RequiresPermissions -> ShowLocalStoragePermissionRequest(
                        it
                    )
                        .takeIf {
                            settingsUseCase.getShowBannerAskingForLocalMediaPermissionsOnFeed()
                        }
                    else -> {
                        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
                        HideLocalStoragePermissionRequest
                    }
                }
            }

    private fun FeedActionsContext.cloudSyncHeader() =
        syncUseCase.observeSyncEnabled().mapNotNull { enabled ->
            ShowCloudSyncRequest.takeIf { !enabled && settingsUseCase.getShowBannerAskingForCloudSyncOnFeed() }
        }

    private fun FeedActionsContext.showClusters() =
        combine(
            feedUseCase.observeFeed(FeedFetchType.ONLY_WITH_DATES).debounce(200),
            selectionList.ids,
            avatarUseCase.getAvatarState(),
            feedUseCase
                .getFeedDisplay()
                .distinctUntilChanged()
        ) { mediaCollections, selectedIds, avatar, feedDisplay ->
            val selected = mediaCollections
                .map { it.toCluster() }
                .selectCels(selectedIds)
            val final = when (feedDisplay) {
                PredefinedCollageDisplay.YEARLY -> selected.groupByYear()
                    .map { it.copy(showRefreshIcon = false) }
                else -> selected
                    .map { it.copy(showRefreshIcon = it.hasAnyCelsWithRemoteMedia) }
            }
            if (avatar.syncState != SyncState.IN_PROGRESS && final.celCount == 0) {
                ShowNoPhotosFound
            } else {
                ShowClusters(final)
            }
        }

    private val List<Cluster>.celCount get() = sumOf { it.cels.size }

    private fun List<Cluster>.selectCels(ids: Set<String>): List<Cluster> {
        val empty = ids.isEmpty()
        return map { cluster ->
            cluster.copy(cels = cluster.cels.map { cel ->
                cel.copy(
                    selectionMode = when {
                        empty -> MediaItemSelectionMode.UNDEFINED
                        cel.mediaItem.id.value.toString() in ids -> MediaItemSelectionMode.SELECTED
                        else -> MediaItemSelectionMode.UNSELECTED
                    }
                )
            }.toPersistentList())
        }
    }

    private fun List<Cluster>.groupByYear() = groupBy {
        it.unformattedDate?.split("-")?.get(0)
    }.map { (year, clusters) ->
        Cluster(
            id = year ?: "-",
            unformattedDate = year ?: "-",
            cels = clusters.flatMap { it.cels }.toPersistentList(),
            displayTitle = year ?: "-",
            location = null,
        )
    }

    private fun FeedActionsContext.changeDisplay() =
        feedUseCase
            .getFeedDisplay()
            .distinctUntilChanged()
            .map(FeedMutation::ChangeDisplay)

    private fun FeedActionsContext.changeItemSyncDisplay() =
        settingsUseCase
            .observeFeedMediaItemSyncDisplay()
            .distinctUntilChanged()
            .map(FeedMutation::ChangeItemSyncDisplay)

    private fun FeedActionsContext.showLibraryTab() =
        settingsUseCase.observeShowLibrary()
            .map(FeedMutation::ShowLibrary)

    private fun FeedActionsContext.setAutoHideNav() =
        settingsUseCase.observeAutoHideFeedNavOnScroll()
            .map(FeedMutation::AutoHideNavBar)

}
