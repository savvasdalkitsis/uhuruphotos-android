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

import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.UserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.NewClusterState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCelState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

sealed class FeedMutation(
    mutation: Mutation<FeedState>,
) : Mutation<FeedState> by mutation {

    data object Loading : FeedMutation({
        it.copyFeed { copy(isLoading = true) }
    })

    data object StartRefreshing : FeedMutation({
        it.copy(isRefreshing = true)
    })

    data object StopRefreshing : FeedMutation({
        it.copy(isRefreshing = false)
    })

    data object ShowTrashingConfirmationDialog : FeedMutation({
        it.copy(showTrashingConfirmationDialog = true)
    })

    data object ShowDeleteConfirmationDialog : FeedMutation({
        it.copy(showDeleteConfirmationDialog = true)
    })

    data object ShowFullySyncedDeleteConfirmationDialog : FeedMutation({
        it.copy(showFullySyncedDeleteConfirmationDialog = true)
    })

    data object HideAllConfirmationDialogs : FeedMutation({
        it.copy(
            showTrashingConfirmationDialog = false,
            showDeleteConfirmationDialog = false,
            showFullySyncedDeleteConfirmationDialog = false,
        )
    })

    data object ShowNoPhotosFound : FeedMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = true, clusters = persistentListOf()) }
    })

    data class ShowClusters(val clusterStates: List<ClusterState>) : FeedMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = false, clusters = clusterStates.toImmutableList()) }
    }) {
        override fun toString() = "ShowClusters(${clusterStates.size})"
    }

    data class ShowFeed(val clusters: List<NewClusterState>) : FeedMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = false, newClusters = clusters.toImmutableList()) }
    }) {
        override fun toString() = "ShowFeed(${clusters.size})"
    }

    data class ChangeDisplay(val display: PredefinedCollageDisplayState) : FeedMutation({
        it.copyFeed { copy(collageDisplayState = display) }
    })

    data class ChangeItemSyncDisplay(val display: FeedMediaItemSyncDisplayState) : FeedMutation({
        it.copy(syncItemDisplay = display)
    })

    data class ShowLibrary(val showLibrary: Boolean) : FeedMutation({
        it.copy(showLibrary = showLibrary)
    })

    data class ShowMemories(val memories: List<MemoryCelState>) : FeedMutation({
        it.copy(memories = memories.toImmutableList())
    })

    data object HideMemories : FeedMutation({
        it.copy(memories = persistentListOf())
    })

    data class ShowLostServerConnection(val show: Boolean) : FeedMutation({
        it.copy(showLoginBanner = show)
    })

    data class ShowBatteryOptimizationBanner(val show: Boolean) : FeedMutation({
        it.copy(showBatteryOptimizationBanner = show)
    })

    data class ShowLocalMediaSyncRunning(val running: Boolean) : FeedMutation({
        it.copy(localMediaSyncRunning = running)
    })

    data class ShowRequestForLocalStoragePermission(val permissions: MediaItemsOnDeviceModel.RequiresPermissionsModel) : FeedMutation({
        it.copy(showRequestPermissionForLocalMediaAccess = permissions)
    })

    data class AskForPermissions(val deniedPermissions: List<String>) : FeedMutation({
        it.copy(missingPermissions = deniedPermissions.toImmutableList())
    })

    data object HideRequestForLocalStoragePermission : FeedMutation({
        it.copy(showRequestPermissionForLocalMediaAccess = null)
    })

    data object ShowRequestForCloudSync : FeedMutation({
        it.copy(showRequestForCloudSync = true)
    })

    data object HideRequestForCloudSync : FeedMutation({
        it.copy(showRequestForCloudSync = false)
    })

    data class AutoHideNavBar(val autoHide: Boolean) : FeedMutation({
        it.copy(autoHideNavOnScroll = autoHide)
    })

    data class ShowAddToAlbumDialog(val albums: List<UserAlbumState>) : FeedMutation({
        it.copy(
            isRefreshing = false,
            showAddToAlbumDialog = albums.toImmutableList(),
        )
    })

    data class FilterAlbums(val filter: String) : FeedMutation({
        it.copy(
            showAddToAlbumDialog = it.showAddToAlbumDialog
                ?.map { album ->
                    album.copy(visible =
                        when {
                            filter.isBlank() -> true
                            else -> (album.title as? Title.Text)?.title.orEmpty().contains(filter, ignoreCase = true)
                        }
                    )
                }
                ?.toImmutableList(),
        )
    })

    data object HideAddToAlbumDialog : FeedMutation({
        it.copy(showAddToAlbumDialog = null)
    })

    data object ShowNewAlbumNameDialog : FeedMutation({
        it.copy(showNewAlbumNameDialog = true)
    })

    data object HideNewAlbumNameDialog : FeedMutation({
        it.copy(showNewAlbumNameDialog = false)
    })
}

private fun FeedState.copyFeed(collageStateMutation: CollageState.() -> CollageState) =
    copy(collageState = collageStateMutation(collageState))
