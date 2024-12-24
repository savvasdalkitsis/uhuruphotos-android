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
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatusModel.BATTERY_OPTIMIZED
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchTypeModel
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.HideMemories
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.HideRequestForLocalStoragePermission
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowBatteryOptimizationBanner
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowClusters
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowLocalMediaSyncRunning
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowLostServerConnection
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowNoPhotosFound
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowRequestForCloudSync
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowRequestForLocalStoragePermission
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCelState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaIdModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel.RequiresPermissionsModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.feature.sync.domain.api.model.SyncStatus.*
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
data object LoadFeed : FeedAction() {

    override fun FeedActionsContext.handle(
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
        checkBatterOptimizations(),
    )

    private fun FeedActionsContext.checkBatterOptimizations() =
        batteryUseCase.observerBatteryOptimizationStatus().distinctUntilChanged().map {
            ShowBatteryOptimizationBanner(it == BATTERY_OPTIMIZED)
        }

    private fun FeedActionsContext.lostServerConnection() =
        welcomeUseCase.observeWelcomeStatus().map {
            ShowLostServerConnection(it.hasLostRemoteAccess)
        }

    private fun FeedActionsContext.memories(): Flow<FeedMutation> =
        combine(
            settingsUIUseCase.observeMemoriesEnabled(),
            settingsUIUseCase.observeMemoriesParallaxEnabled(),
        ) { enabled, parallax ->
            enabled to parallax
        }.flatMapLatest { (enabled, parallax) ->
            if (enabled) {
                memoriesUseCase.observeMemories().map { memoryCollections ->
                    memoryCollections.map { (collection, yearsAgo) ->
                        MemoryCelState(
                            yearsAgo = yearsAgo,
                            cels = collection.mediaItems.map { it.toCel() }.toImmutableList(),
                            parallaxEnabled = parallax,
                        )
                    }
                }.map(FeedMutation::ShowMemories)
            } else {
                flowOf(HideMemories)
            }
        }

    private fun FeedActionsContext.localMediaSyncStatus() =
        jobsUseCase.observeJobsStatusFilteredBySettings().map { it.jobs[JobModel.LOCAL_MEDIA_SYNC] }.map {
            ShowLocalMediaSyncRunning(it == JobStatusModel.QueuedModel || it is JobStatusModel.InProgressModel)
        }.debounce(4000)

    private fun FeedActionsContext.localMediaPermissionHeader() =
        mediaUseCase.observeLocalMedia()
            .mapNotNull {
                when (it) {
                    is RequiresPermissionsModel -> ShowRequestForLocalStoragePermission(it)
                        .takeIf {
                            settingsUIUseCase.getShowBannerAskingForLocalMediaPermissionsOnFeed()
                        }
                    else -> {
                        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
                        HideRequestForLocalStoragePermission
                    }
                }
            }

    private fun FeedActionsContext.cloudSyncHeader() =
        combine(
            welcomeUseCase.observeWelcomeStatus(),
            syncUseCase.observeSyncStatus(),
        ) { welcomeStatus, syncStatus ->
            ShowRequestForCloudSync.takeIf {
                welcomeStatus.hasRemoteAccess
                        && syncStatus != Enabled
                        && settingsUIUseCase.getShowBannerAskingForCloudSyncOnFeed()
            }
        }.filterNotNull()

    private fun FeedActionsContext.showClusters() =
        combine(
            feedUseCase.observeFeed(FeedFetchTypeModel.ONLY_WITH_DATES).debounce(200),
            selectionList.ids,
            avatarUseCase.getAvatarState(),
            feedUseCase
                .observeFeedDisplay()
                .distinctUntilChanged(),
        ) { mediaCollections, selectedIds, avatar, feedDisplay ->
            val selected = mediaCollections
                .map { it.toCluster() }
                .selectCels(selectedIds)
            val final = when (feedDisplay) {
                PredefinedCollageDisplayState.YEARLY -> selected.groupByYear()
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

    private val List<ClusterState>.celCount get() = sumOf { it.cels.size }

    private fun List<ClusterState>.selectCels(ids: Set<MediaIdModel<*>>): List<ClusterState> {
        val empty = ids.isEmpty()
        return map { cluster ->
            cluster.copy(cels = cluster.cels.map { cel ->
                cel.copy(
                    selectionMode = when {
                        empty -> SelectionMode.UNDEFINED
                        cel.mediaItem.id in ids -> SelectionMode.SELECTED
                        else -> SelectionMode.UNSELECTED
                    }
                )
            }.toImmutableList())
        }
    }

    private fun List<ClusterState>.groupByYear() = groupBy {
        it.unformattedDate?.split("-")?.get(0)
    }.map { (year, clusters) ->
        ClusterState(
            id = year ?: "-",
            unformattedDate = year ?: "-",
            cels = clusters.flatMap { it.cels }.toImmutableList(),
            displayTitle = year ?: "-",
            location = null,
        )
    }

    private fun FeedActionsContext.changeDisplay() =
        feedUseCase
            .observeFeedDisplay()
            .distinctUntilChanged()
            .map(FeedMutation::ChangeDisplay)

    private fun FeedActionsContext.changeItemSyncDisplay() =
        settingsUIUseCase
            .observeFeedMediaItemSyncDisplay()
            .distinctUntilChanged()
            .map(FeedMutation::ChangeItemSyncDisplay)

    private fun FeedActionsContext.showLibraryTab() =
        settingsUIUseCase.observeShowLibrary()
            .map(FeedMutation::ShowLibrary)

    private fun FeedActionsContext.setAutoHideNav() =
        settingsUIUseCase.observeAutoHideFeedNavOnScroll()
            .map(FeedMutation::AutoHideNavBar)

}
