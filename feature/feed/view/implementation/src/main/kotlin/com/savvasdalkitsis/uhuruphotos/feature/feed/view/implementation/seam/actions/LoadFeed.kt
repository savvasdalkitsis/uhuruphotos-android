package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import androidx.work.WorkInfo
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.toCel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.isActive

object LoadFeed : FeedAction() {

    context(FeedActionsContext) override fun handle(
        state: FeedState,
        effect: EffectHandler<FeedEffect>
    ) = merge(
        showLibraryTab(),
        changeDisplay(),
        flowOf(FeedMutation.Loading),
        showClusters(),
        localMediaPermissionHeader(),
        localMediaSyncStatus(),
        memories(),
    )

    private fun FeedActionsContext.memories() =
        settingsUseCase.observeMemoriesEnabled().flatMapLatest { enabled ->
            if (enabled) {
                memoriesUseCase.observeMemories().flatMapLatest { memoryCollections ->
                    flow {
                        var index = 0
                        while (currentCoroutineContext().isActive) {
                            index++
                            emit(memoryCollections.map { (collection, yearsAgo) ->
                                MemoryCel(
                                    yearsAgo = yearsAgo,
                                    cel = collection.mediaItems[index % collection.mediaItems.size].toCel()
                                )
                            })
                            delay(6000)
                        }
                    }
                }.map(FeedMutation::ShowMemories)
            } else {
                flowOf(FeedMutation.HideMemories)
            }
        }

    private fun FeedActionsContext.localMediaSyncStatus() =
        mediaUseCase.observeLocalMediaSyncJobStatus().map {
            FeedMutation.ShowLocalMediaSyncRunning(it == WorkInfo.State.ENQUEUED || it == WorkInfo.State.RUNNING)
        }

    private fun FeedActionsContext.localMediaPermissionHeader() =
        mediaUseCase.observeLocalMedia()
            .mapNotNull {
                when (it) {
                    is MediaItemsOnDevice.RequiresPermissions -> FeedMutation.ShowLocalStoragePermissionRequest(
                        it
                    )
                        .takeIf {
                            settingsUseCase.getShowBannerAskingForLocalMediaPermissionsOnFeed()
                        }
                    else -> {
                        localMediaWorkScheduler.scheduleLocalMediaSyncNowIfNotRunning()
                        FeedMutation.HideLocalStoragePermissionRequest
                    }
                }
            }

    private fun FeedActionsContext.showClusters() =
        combine(
            feedUseCase.observeFeed().debounce(200),
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
                FeedMutation.ShowNoPhotosFound
            } else {
                FeedMutation.ShowClusters(final)
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
            })
        }
    }

    private fun List<Cluster>.groupByYear() = groupBy {
        it.unformattedDate?.split("-")?.get(0)
    }.map { (year, clusters) ->
        Cluster(
            id = year ?: "-",
            unformattedDate = year ?: "-",
            cels = clusters.flatMap { it.cels },
            displayTitle = year ?: "-",
            location = null,
        )
    }

    private fun FeedActionsContext.changeDisplay() =
        feedUseCase
            .getFeedDisplay()
            .distinctUntilChanged()
            .map(FeedMutation::ChangeDisplay)

    private fun FeedActionsContext.showLibraryTab() =
        settingsUseCase.observeShowLibrary()
            .map(FeedMutation::ShowLibrary)

}