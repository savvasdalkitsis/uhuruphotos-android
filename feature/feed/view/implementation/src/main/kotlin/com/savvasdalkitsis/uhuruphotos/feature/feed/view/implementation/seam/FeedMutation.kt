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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.MemoryCel
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemsOnDevice
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation

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
        it.copyFeed { copy(isLoading = false, isEmpty = true, clusters = emptyList()) }
    })

    data class ShowClusters(val clusters: List<Cluster>) : FeedMutation({
        it.copyFeed { copy(isLoading = false, isEmpty = false, clusters = clusters) }
    }) {
        override fun toString() = "ShowClusters(${clusters.size})"
    }

    data class ChangeDisplay(val display: PredefinedCollageDisplay) : FeedMutation({
        it.copyFeed { copy(collageDisplay = display) }
    })

    data class ShowLibrary(val showLibrary: Boolean) : FeedMutation({
        it.copy(showLibrary = showLibrary)
    })

    data class ShowMemories(val memories: List<MemoryCel>) : FeedMutation({
        it.copy(memories = memories)
    })

    data object HideMemories : FeedMutation({
        it.copy(memories = emptyList())
    })

    data class ShowLocalMediaSyncRunning(val running: Boolean) : FeedMutation({
        it.copy(localMediaSyncRunning = running)
    })

    data class ShowLocalStoragePermissionRequest(val permissions: MediaItemsOnDevice.RequiresPermissions) : FeedMutation({
        it.copy(showRequestPermissionForLocalMediaAccess = permissions)
    })

    data class AskForPermissions(val deniedPermissions: List<String>) : FeedMutation({
        it.copy(missingPermissions = deniedPermissions)
    })

    data object HideLocalStoragePermissionRequest : FeedMutation({
        it.copy(showRequestPermissionForLocalMediaAccess = null)
    })
}

private fun FeedState.copyFeed(collageStateMutation: CollageState.() -> CollageState) =
    copy(collageState = collageStateMutation(collageState))
