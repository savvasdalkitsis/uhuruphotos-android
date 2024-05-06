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

import android.content.pm.ResolveInfo
import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.domain.api.model.LightboxDetails
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.toLightboxDetailsState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import dev.icerock.moko.resources.StringResource
import kotlinx.collections.immutable.toPersistentList
import kotlin.math.min

sealed class LightboxMutation(
    mutation: Mutation<LightboxState>,
) : Mutation<LightboxState> by mutation {

    data object HideUI : LightboxMutation({
        it.copy(showUI = false)
    })

    data object ShowUI : LightboxMutation({
        it.copy(showUI = true)
    })

    data object DismissErrorMessage : LightboxMutation({
        it.copy(errorMessage = null)
    })

    data class ShowErrorMessage(val message: StringResource) : LightboxMutation({
        it.copy(
            isLoading = false,
            errorMessage = message,
        )
    })

    data object FinishedLoading : LightboxMutation({
        it.copy(
            isLoading = false,
        )
    })

    data object Loading : LightboxMutation({
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

    data object ShowInfo : LightboxMutation({
        it.copy(infoSheetHidden = false)
    })

    data object HideInfo : LightboxMutation({
        it.copy(infoSheetHidden = true)
    })

    data object ShowDeleteConfirmationDialog : LightboxMutation({
        it.copy(showDeleteConfirmationDialog = true)
    })

    data object ShowFullySyncedDeleteConfirmationDialog : LightboxMutation({
        it.copy(showFullySyncedDeleteConfirmationDialog = true)
    })

    data object ShowRemoteTrashingConfirmationDialog : LightboxMutation({
        it.copy(showTrashingConfirmationDialog = true)
    })

    data object ShowRestorationConfirmationDialog : LightboxMutation({
        it.copy(showRestorationConfirmationDialog = true)
    })

    data object HideAllConfirmationDialogs : LightboxMutation({
        it.copy(
            showDeleteConfirmationDialog = false,
            showTrashingConfirmationDialog = false,
            showRestorationConfirmationDialog = false,
            showFullySyncedDeleteConfirmationDialog = false,
            showCannotUploadDialog = false,
            showCannotCheckUploadStatusDialog = false,
        )
    })

    data class ShowMedia(
        val mediaItemStates: List<SingleMediaItemState>,
        val index: Int,
    ) : LightboxMutation({
        val current = if (it.media.isNotEmpty()) it.currentMediaItem else null
        it.copyWithIndex(
            index = index,
        ).copy(
            media = mediaItemStates.reuse(current).toPersistentList(),
        )
    }) {
        override fun toString() = "ShowMedia [index: $index, size:${mediaItemStates.size}, current: ${mediaItemStates[index]}]"
    }

    data class ReceivedDetails(
        val id: MediaId<*>,
        val details: LightboxDetails,
        val serverUrl: String,
    ) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(
                details = details.toLightboxDetailsState(serverUrl),
            )
        }
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
            media = it.media.filter { photoState -> photoState.id != id }.toPersistentList(),
        )
        removed.copyWithIndex(
            index = min(it.currentIndex, removed.media.size - 1)
        )
    })

    data class ReplaceMediaItemInSource(val id: MediaId<*>, val newItem: SingleMediaItemState) : LightboxMutation({
        it.copyItem(id) {
            newItem
        }
    })

    data class AskForPermissions(val deniedPermissions: List<String>) : LightboxMutation({
        it.copy(missingPermissions = deniedPermissions.toPersistentList())
    })

    class ShowEditOptions(id: MediaId<*>, apps: List<ResolveInfo>) : LightboxMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(showEditApps = apps.toPersistentList())
        }
    })

    data object ShowRestoreButton : LightboxMutation({
        it.copy(showRestoreButton = true)
    })

    data object ShowCannotUploadDialog : LightboxMutation({
        it.copy(showCannotUploadDialog = true)
    })

    data object ShowCannotCheckUploadStatusDialog : LightboxMutation({
        it.copy(showCannotCheckUploadStatusDialog = true)
    })

    data class ShowUpsellDialog(val show: Boolean) : LightboxMutation({
        it.copy(showUpsellDialog = show)
    })

    data class ShowActionsOverlay(val show: Boolean) : LightboxMutation({
        it.copy(showActionsOverlay = show)
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
}.toPersistentList())


private fun List<SingleMediaItemState>.reuse(current: SingleMediaItemState?): List<SingleMediaItemState> =
    when (current) {
        null -> this
        else -> map {
            when (it.id) {
                current.id -> current
                else -> it
            }
        }
    }