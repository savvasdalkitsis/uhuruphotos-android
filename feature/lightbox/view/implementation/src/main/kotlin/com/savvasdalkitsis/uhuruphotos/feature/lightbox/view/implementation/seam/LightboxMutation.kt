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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam

import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.OriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.StoragePermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemMetadata
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlin.math.min

sealed class LightboxMutation(
    mutation: Mutation<LightboxState>,
) : Mutation<LightboxState> by mutation {

    object HideUI : LightboxMutation({
        it.copy(showUI = false)
    })

    object ShowUI : LightboxMutation({
        it.copy(showUI = true)
    })

    object DismissErrorMessage : LightboxMutation({
        it.copy(errorMessage = null)
    })

    data class ShowErrorMessage(@StringRes val message: Int) : LightboxMutation({
        it.copy(
            isLoading = false,
            errorMessage = message,
        )
    })

    object FinishedLoading : LightboxMutation({
        it.copy(
            isLoading = false,
            showInfoButton = true,
        )
    })

    object Loading : LightboxMutation({
        it.copy(
            isLoading = true,
        )
    })

    data class LoadingDetails(val id: MediaId<*>) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(loadingDetails = true)
        }
    })

    data class FinishedLoadingDetails(val id: MediaId<*>) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(loadingDetails = false)
        }
    })

    object ShowInfo : LightboxMutation({
        it.copy(infoSheetHidden = false)
    })

    object HideInfo : LightboxMutation({
        it.copy(infoSheetHidden = true)
    })

    object ShowDeleteConfirmationDialog : LightboxMutation({
        it.copy(showDeleteConfirmationDialog = true)
    })

    object ShowFullySyncedDeleteConfirmationDialog : LightboxMutation({
        it.copy(showFullySyncedDeleteConfirmationDialog = true)
    })

    data class ShowStorageManagementConfirmationDialog(val request: StoragePermissionRequest) : LightboxMutation({
        it.copy(showStorageManagementConfirmationDialog = request)
    })

    object ShowRemoteTrashingConfirmationDialog : LightboxMutation({
        it.copy(showTrashingConfirmationDialog = true)
    })

    object ShowRestorationConfirmationDialog : LightboxMutation({
        it.copy(showRestorationConfirmationDialog = true)
    })

    object HideAllConfirmationDialogs : LightboxMutation({
        it.copy(
            showDeleteConfirmationDialog = false,
            showTrashingConfirmationDialog = false,
            showRestorationConfirmationDialog = false,
            showFullySyncedDeleteConfirmationDialog = false,
            showStorageManagementConfirmationDialog = null,
        )
    })

    data class ShowShareIcon(val id: MediaId<*>) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(showShareIcon = true)
        }
    })

    data class ShowUseAsIcon(val id: MediaId<*>) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(showUseAsIcon = true)
        }
    })

    data class SetOriginalFileIconState(val id: MediaId<*>, val state: OriginalFileIconState) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(originalFileIconState = state)
        }
    })

    data class ShowMetadata(val id: MediaId<*>, val metadata: MediaItemMetadata) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(metadata = metadata)
        }
    })

    data class ShowSingleMediaItem(val mediaItemState: SingleMediaItemState) : LightboxMutation({
        it.copyWithIndex(
            index = 0,
        ).copy(
            media = listOf(mediaItemState)
        )
    })

    data class ShowMultipleMedia(
        val mediaItemStates: List<SingleMediaItemState>,
        val index: Int,
    ) : LightboxMutation({
        it.copyWithIndex(
            index = index,
        ).copy(
            media = mediaItemStates,
        )
    }) {
        override fun toString() = "ShowMultipleMedia [index: $index, size:${mediaItemStates.size}]"
    }

    data class ReceivedDetails(
        val id: MediaId<*>,
        val details: MediaItemDetails,
    ) : LightboxMutation({
        with(details) {
            it.copyItem(id) { photoState ->
                photoState.copy(
                    isFavourite = isFavourite,
                    isVideo = isVideo,
                    dateAndTime = formattedDateAndTime,
                    location = location,
                    gps = latLon,
                    peopleInMediaItem = peopleInMediaItem,
                    remotePath = remotePath,
                    localPath = localPath,
                )
            }
        }
    })

    data class ChangeCurrentIndex(val index: Int) : LightboxMutation({
        it.copyWithIndex(index = index)
    })

    data class ShowMediaItemFavourite(
        val id: MediaId<*>,
        val favourite: Boolean,
    ) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(isFavourite = favourite)
        }
    })

    data class RemoveMediaItemFromSource(val id: MediaId<*>) : LightboxMutation({
        val removed = it.copy(
            media = it.media.filter { photoState -> photoState.id != id },
        )
        removed.copyWithIndex(
            index = min(it.currentIndex, removed.media.size - 1)
        )
    })

    data class AskForPermissions(val deniedPermissions: List<String>) : LightboxMutation({
        it.copy(missingPermissions = deniedPermissions)
    })

    object ShowRestoreButton : LightboxMutation({
        it.copy(showRestoreButton = true)
    })
}

private fun LightboxState.copyItem(
    id: MediaId<*>,
    copy: (SingleMediaItemState) -> SingleMediaItemState
): LightboxState = copy(media = media.map { mediaItem ->
    when (mediaItem.id) {
        id -> copy(mediaItem)
        else -> mediaItem
    }
})