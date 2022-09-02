/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.api.userbadge.ui.state.SyncState.IN_PROGRESS
import com.savvasdalkitsis.uhuruphotos.api.userbadge.usecase.UserBadgeUseCase
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay.YEARLY
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.toCluster
import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.usecase.FeedUseCase
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.SelectionList
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.AskForSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.CelLongPressed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ChangeDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ClearSelected
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ClusterRefreshClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ClusterSelectionClicked
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.DismissSelectedPhotosTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.DownloadSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.LoadFeed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.RefreshFeed
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.SelectedCel
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.ShareSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedAction.TrashSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.DownloadingFiles
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.Share
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.Vibrate
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.HideTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowClusters
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowLibrary
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowNoPhotosFound
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.ShowTrashingConfirmationDialog
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.StartRefreshing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.StopRefreshing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode.SELECTED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode.UNDEFINED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode.UNSELECTED
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.usecase.MediaUseCase
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

internal class FeedActionHandler @Inject constructor(
    private val userBadgeUseCase: UserBadgeUseCase,
    private val feedUseCase: FeedUseCase,
    private val mediaUseCase: MediaUseCase,
    private val selectionList: SelectionList,
    private val settingsUseCase: SettingsUseCase,
) : ActionHandler<FeedState, FeedEffect, FeedAction, FeedMutation> {

    override fun handleAction(
        state: FeedState,
        action: FeedAction,
        effect: suspend (FeedEffect) -> Unit,
    ): Flow<FeedMutation> = when (action) {
        is LoadFeed -> merge(
            settingsUseCase.observeShowLibrary()
                .map(::ShowLibrary),
            feedUseCase
                .getFeedDisplay()
                .distinctUntilChanged()
                .map(FeedMutation::ChangeDisplay),
            flowOf(Loading),
            combine(
                feedUseCase.observeFeed().debounce(200),
                selectionList.ids,
                userBadgeUseCase.getUserBadgeState(),
                feedUseCase
                    .getFeedDisplay()
                    .distinctUntilChanged()
            ) { mediaCollections, ids, userBadge, feedDisplay ->
                val selected = mediaCollections
                    .map { it.toCluster() }
                    .selectCels(ids)
                val final = when (feedDisplay) {
                    YEARLY -> selected.groupByYear()
                    else -> selected
                }
                if (userBadge.syncState != IN_PROGRESS && final.celCount == 0) {
                    ShowNoPhotosFound
                } else {
                    ShowClusters(final)
                }
            },
        )
        RefreshFeed -> flow {
            emit(StartRefreshing)
            feedUseCase.startRefreshFeedWork(shallow = true)
            delay(200)
            emit(StopRefreshing)
        }
        is SelectedCel -> flow {
            when {
                state.selectedCelCount == 0 -> effect(with(action) {
                    OpenLightbox(celState.mediaItem.id, center, scale, celState.mediaItem.isVideo)
                })
                action.celState.selectionMode == SELECTED -> {
                    effect(Vibrate)
                    action.celState.deselect()
                }
                else -> {
                    effect(Vibrate)
                    action.celState.select()
                }
            }
        }
        is ChangeDisplay -> flow {
            feedUseCase.setFeedDisplay(action.display)
        }
        is CelLongPressed -> flow {
            if (state.selectedCelCount == 0) {
                effect(Vibrate)
                action.celState.select()
            }
        }
        ClearSelected -> flow {
            effect(Vibrate)
            selectionList.clear()
        }
        AskForSelectedPhotosTrashing -> flowOf(ShowTrashingConfirmationDialog)
        is ClusterSelectionClicked -> flow {
            val cels = action.cluster.cels
            effect(Vibrate)
            if (cels.all { it.selectionMode == SELECTED }) {
                cels.forEach { it.deselect() }
            } else {
                cels.forEach { it.select() }
            }
        }
        is ClusterRefreshClicked -> flow {
            emit(StartRefreshing)
            feedUseCase.refreshCluster(action.cluster.id)
            emit(StopRefreshing)
        }
        DismissSelectedPhotosTrashing -> flowOf(HideTrashingConfirmationDialog)
        TrashSelectedCels -> flow {
            emit(HideTrashingConfirmationDialog)
            state.selectedCels.forEach {
                mediaUseCase.trashMediaItem(it.mediaItem.id)
            }
            selectionList.clear()
        }
        ShareSelectedCels -> flow {
            effect(Share(state.selectedCels))
        }
        DownloadSelectedCels -> flow {
            effect(DownloadingFiles)
            state.selectedCels.forEach {
                mediaUseCase.downloadOriginal(it.mediaItem.id, it.mediaItem.isVideo)
            }
        }
    }

    private suspend fun CelState.deselect() {
        selectionList.deselect(mediaItem.id)
    }

    private suspend fun CelState.select() {
        selectionList.select(mediaItem.id)
    }

    private val List<Cluster>.celCount get() = sumOf { it.cels.size }

    private fun List<Cluster>.selectCels(ids: Set<String>): List<Cluster> {
        val empty = ids.isEmpty()
        return map { cluster ->
            cluster.copy(cels = cluster.cels.map { cel ->
                cel.copy(selectionMode = when {
                    empty -> UNDEFINED
                    cel.mediaItem.id.value.toString() in ids -> SELECTED
                    else -> UNSELECTED
                })
            })
        }
    }

    private fun List<Cluster>.groupByYear() = groupBy {
        it.unformattedDate?.split("-")?.get(0)
    }.map { (year, clusters) ->
        Cluster(
            id = year ?: "-",
            cels = clusters.flatMap { it.cels },
            displayTitle = year ?: "-",
            location = null,
        )
    }
}
