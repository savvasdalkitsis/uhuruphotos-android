/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DeleteLocalSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.DismissSelectedMediaTrashing
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.FeedAction
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.MissingPermissionsRequested
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.TrashRemoteAndDeleteLocalSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions.TrashRemoteSelectedCels
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeleteFullySyncedPermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.DeletePermissionDialog
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.TrashPermissionDialog
import com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui.MissingPermissionsRequest

@Composable
internal fun FeedDialogs(
    state: FeedState,
    action: (FeedAction) -> Unit,
) {
    if (state.showTrashingConfirmationDialog) {
        TrashPermissionDialog(
            mediaItemCount = state.selectedCelCount,
            onDismiss = { action(DismissSelectedMediaTrashing) },
            onDelete = { action(TrashRemoteSelectedCels) },
        )
    }
    if (state.showDeleteConfirmationDialog) {
        DeletePermissionDialog(
            mediaItemCount = state.selectedCelCount,
            onDismiss = { action(DismissSelectedMediaTrashing) },
            onDelete = { action(DeleteLocalSelectedCels) },
        )
    }
    if (state.showFullySyncedDeleteConfirmationDialog) {
        DeleteFullySyncedPermissionDialog(
            onDismiss = { action(DismissSelectedMediaTrashing) },
            onDeleteLocalTrashRemote = { action(TrashRemoteAndDeleteLocalSelectedCels) },
            onDeleteLocal = { action(DeleteLocalSelectedCels) },
        )
    }
    MissingPermissionsRequest(state.missingPermissions) {
        action(MissingPermissionsRequested)
    }
    state.showAddToAlbumDialog?.let { albums ->
        AddToAlbumDialog(albums, action)
    }
    if (state.showNewAlbumNameDialog) {
        NewAlbumNameDialog(action)
    }
}
