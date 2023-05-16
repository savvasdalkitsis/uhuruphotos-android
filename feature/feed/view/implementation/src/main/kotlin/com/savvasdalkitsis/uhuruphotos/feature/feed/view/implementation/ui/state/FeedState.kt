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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplay
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState

data class FeedState(
    val collageState: CollageState = CollageState(),
    val isRefreshing: Boolean = false,
    val showLibrary: Boolean = true,
    val showTrashingConfirmationDialog: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false,
    val showFullySyncedDeleteConfirmationDialog: Boolean = false,
    val showRequestPermissionForLocalMediaAccess: MediaItemsOnDevice.RequiresPermissions? = null,
    val localMediaSyncRunning: Boolean = false,
    val memories: List<MemoryCel> = emptyList(),
    val missingPermissions: List<String> = emptyList(),
    val syncItemDisplay: FeedMediaItemSyncDisplay = FeedMediaItemSyncDisplay.default,
) {
    val selectedCelCount: Int = collageState.clusters.sumOf { cluster ->
        cluster.cels.count { cel ->
            cel.selectionMode == MediaItemSelectionMode.SELECTED
        }
    }
    val hasSelection = selectedCelCount > 0
    val selectedCels: List<CelState> = collageState.clusters.flatMap { cluster ->
        cluster.cels.filter { cel ->
            cel.selectionMode == MediaItemSelectionMode.SELECTED
        }
    }
    val shouldShowShareIcon: Boolean = selectedCels.let { selected ->
        selected.isNotEmpty() && selected.none { it.mediaItem.id.isVideo }
    }
    val shouldShowDeleteIcon: Boolean = selectedCels.syncStates.size == 1

    val shouldShowDownloadIcon: Boolean = selectedCels.let { selected ->
        selected.isNotEmpty() && selected.none { it.mediaItem.id.findLocal != null }
    }

}

val List<CelState>.syncStates get() = map { it.mediaItem.id.syncState }.toSet()
