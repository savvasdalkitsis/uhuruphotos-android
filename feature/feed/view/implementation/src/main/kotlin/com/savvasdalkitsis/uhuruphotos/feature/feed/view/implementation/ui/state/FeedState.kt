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

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.catalogue.user.view.api.state.UserAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.api.ui.state.FeedMediaItemSyncDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDeviceModel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class FeedState(
    val collageState: CollageState = CollageState(),
    val isRefreshing: Boolean = false,
    val showLibrary: Boolean = true,
    val showTrashingConfirmationDialog: Boolean = false,
    val showDeleteConfirmationDialog: Boolean = false,
    val showFullySyncedDeleteConfirmationDialog: Boolean = false,
    val showRequestPermissionForLocalMediaAccess: MediaItemsOnDeviceModel.RequiresPermissionsModel? = null,
    val showLoginBanner: Boolean = false,
    val showBatteryOptimizationBanner: Boolean = false,
    val localMediaSyncRunning: Boolean = false,
    val autoHideNavOnScroll: Boolean = true,
    val showAddToAlbumDialog: ImmutableList<UserAlbumState>? = null,
    val showNewAlbumNameDialog: Boolean = false,
    val memories: ImmutableList<MemoryCelState> = persistentListOf(),
    val missingPermissions: ImmutableList<String> = persistentListOf(),
    val syncItemDisplay: FeedMediaItemSyncDisplayState = FeedMediaItemSyncDisplayState.default,
    val showRequestForCloudSync: Boolean = false,
) : Parcelable {
    val selectedCels: ImmutableList<CelState> = collageState.clusters.flatMap { cluster ->
        cluster.cels.filter { cel ->
            cel.selectionMode == SelectionMode.SELECTED
        }
    }.toImmutableList()

    val selectedCelCount: Int = selectedCels.size

    val hasSelection = selectedCelCount > 0

    val shouldShowShareIcon: Boolean =
        selectedCels.isNotEmpty() && selectedCels.none { it.mediaItem.id.isVideo }

    val shouldShowAddIcon: Boolean =
        selectedCels.isNotEmpty() && selectedCels.all { it.mediaItem.id.hasRemote }

    val shouldShowDeleteIcon: Boolean = selectedCels.syncStates.size == 1

    val shouldShowDownloadIcon: Boolean =
        selectedCels.isNotEmpty() && selectedCels.none { it.mediaItem.id.findLocals.isNotEmpty() }

    val shouldShowUploadIcon: Boolean =
        selectedCels.isNotEmpty() && selectedCels.none { it.mediaItem.id.hasRemote }
}

val List<CelState>.syncStates get() = map { it.mediaItem.id.syncState }.toSet()
