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
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState

internal data class FeedState(
    val collageState: CollageState = CollageState(),
    val isRefreshing: Boolean = false,
    val showLibrary: Boolean = true,
    val showTrashingConfirmationDialog: Boolean = false,
    val showRequestPermissionForLocalMediaAccess: MediaItemsOnDevice.RequiresPermissions? = null,
    val localMediaSyncRunning: Boolean = false,
    val memories: List<MemoryCel> = emptyList(),
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
        selected.isNotEmpty() && selected.none{ it.mediaItem.isVideo }
    }
    val shouldShowClusterRefreshButtons: Boolean = collageState.collageDisplay != PredefinedCollageDisplay.YEARLY
}