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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state

import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.UserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class FeedState(
    val collageState: CollageState = CollageState(),
    val isRefreshing: Boolean = false,
    val showLibrary: Boolean = true,
    val showTrashingConfirmationDialog: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false,
    val showFullySyncedDeleteConfirmationDialog: Boolean = false,
    val showRequestPermissionForLocalMediaAccess: MediaItemsOnDevice.RequiresPermissions? = null,
    val showLoginBanner: Boolean = false,
    val showBatteryOptimizationBanner: Boolean = false,
    val localMediaSyncRunning: Boolean = false,
    val autoHideNavOnScroll: Boolean = true,
    val showAddToAlbumDialog: ImmutableList<UserAlbumState>? = null,
    val showNewAlbumNameDialog: Boolean = false,
    val memories: ImmutableList<MemoryCel> = persistentListOf(),
    val missingPermissions: ImmutableList<String> = persistentListOf(),
    val syncItemDisplay: FeedMediaItemSyncDisplay = FeedMediaItemSyncDisplay.default,
    val showRequestForCloudSync: Boolean = false,
) {
    val selectedCelCount: Int = collageState.clusters.sumOf { cluster ->
        cluster.cels.count { cel ->
            cel.selectionMode == SelectionMode.SELECTED
        }
    }
    val hasSelection = selectedCelCount > 0
    val selectedCels: List<CelState> = collageState.clusters.flatMap { cluster ->
        cluster.cels.filter { cel ->
            cel.selectionMode == SelectionMode.SELECTED
        }
    }
    val shouldShowShareIcon: Boolean = selectedCels.let { selected ->
        selected.isNotEmpty() && selected.none { it.mediaItem.id.isVideo }
    }
    val shouldShowAddIcon: Boolean = selectedCels.let { selected ->
        selected.isNotEmpty() && selected.all { it.mediaItem.id.findRemote != null }
    }
    val shouldShowDeleteIcon: Boolean = selectedCels.syncStates.size == 1

    val shouldShowDownloadIcon: Boolean = selectedCels.let { selected ->
        selected.isNotEmpty() && selected.none { it.mediaItem.id.findLocals.isNotEmpty() }
    }

    val shouldShowUploadIcon: Boolean = selectedCels.let { selected ->
        selected.isNotEmpty() && selected.none { it.mediaItem.id.findRemote != null }
    }

}

val List<CelState>.syncStates get() = map { it.mediaItem.id.syncState }.toSet()
