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
package com.savvasdalkitsis.uhuruphotos.implementation.media.page.seam

import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.api.media.page.domain.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.domain.usecase.MediaItemMetadata
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.MediaItemPageState
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.OriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.implementation.media.page.view.state.SingleMediaItemState
import kotlin.math.min

sealed class MediaItemPageMutation(
    mutation: Mutation<MediaItemPageState>,
) : Mutation<MediaItemPageState> by mutation {

    object HideUI : MediaItemPageMutation({
        it.copy(showUI = false)
    })

    object ShowUI : MediaItemPageMutation({
        it.copy(showUI = true)
    })

    object DismissErrorMessage : MediaItemPageMutation({
        it.copy(errorMessage = null)
    })

    data class ShowErrorMessage(@StringRes val message: Int) : MediaItemPageMutation({
        it.copy(
            isLoading = false,
            errorMessage = message,
        )
    })

    object FinishedLoading : MediaItemPageMutation({
        it.copy(
            isLoading = false,
            showInfoButton = true,
        )
    })

    object Loading : MediaItemPageMutation({
        it.copy(
            isLoading = true,
        )
    })

    data class LoadingDetails(val id: String) : MediaItemPageMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(loadingDetails = true)
        }
    })

    data class FinishedLoadingDetails(val id: String) : MediaItemPageMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(loadingDetails = false)
        }
    })

    object ShowInfo : MediaItemPageMutation({
        it.copy(infoSheetHidden = false)
    })

    object HideInfo : MediaItemPageMutation({
        it.copy(infoSheetHidden = true)
    })

    object ShowDeleteConfirmationDialog : MediaItemPageMutation({
        it.copy(showDeleteConfirmationDialog = true)
    })

    object ShowTrashingConfirmationDialog : MediaItemPageMutation({
        it.copy(showTrashingConfirmationDialog = true)
    })

    object ShowRestorationConfirmationDialog : MediaItemPageMutation({
        it.copy(showRestorationConfirmationDialog = true)
    })

    object HideAllConfirmationDialogs : MediaItemPageMutation({
        it.copy(
            showDeleteConfirmationDialog = false,
            showTrashingConfirmationDialog = false,
            showRestorationConfirmationDialog = false,
        )
    })

    data class ShowShareIcon(val id: String) : MediaItemPageMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(showShareIcon = true)
        }
    })

    data class ShowUseAsIcon(val id: String) : MediaItemPageMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(showUseAsIcon = true)
        }
    })

    data class SetOriginalFileIconState(val id: String, val state: OriginalFileIconState) : MediaItemPageMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(originalFileIconState = state)
        }
    })

    data class ShowMetadata(val id: String, val metadata: MediaItemMetadata) : MediaItemPageMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(metadata = metadata)
        }
    })

    data class ShowSingleMediaItem(val mediaItemState: SingleMediaItemState) : MediaItemPageMutation({
        it.copy(
            currentIndex = 0,
            media = listOf(mediaItemState)
        )
    })

    data class ShowMultipleMedia(
        val mediaItemStates: List<SingleMediaItemState>,
        val index: Int,
    ) : MediaItemPageMutation({
        it.copy(
            currentIndex = index,
            media = mediaItemStates,
        )
    }) {
        override fun toString() = "ShowMultipleMedia [index: $index, size:${mediaItemStates.size}]"
    }

    data class ReceivedDetails(
        val id: String,
        val details: MediaItemDetails,
    ) : MediaItemPageMutation({
        with(details) {
            it.copyItem(id) { photoState ->
                photoState.copy(
                    isFavourite = isFavourite,
                    isVideo = isVideo,
                    dateAndTime = formattedDateAndTime,
                    location = location,
                    gps = latLon,
                    peopleInMediaItem = peopleInMediaItem,
                    path = path,
                )
            }
        }
    })

    data class ChangeCurrentIndex(val index: Int) : MediaItemPageMutation({
        it.copy(currentIndex = index)
    })

    data class ShowMediaItemFavourite(
        val id: String,
        val favourite: Boolean,
    ) : MediaItemPageMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(isFavourite = favourite)
        }
    })

    data class RemoveMediaItemFromSource(val id: String) : MediaItemPageMutation({
        val removed = it.copy(
            media = it.media.filter { photoState -> photoState.id != id },
        )
        removed.copy(
            currentIndex = min(it.currentIndex, removed.media.size - 1)
        )
    })

    object ShowRestoreButton : MediaItemPageMutation({
        it.copy(showRestoreButton = true)
    })
}

private fun MediaItemPageState.copyItem(
    id: String,
    copy: (SingleMediaItemState) -> SingleMediaItemState
): MediaItemPageState = copy(media = media.map { mediaItem ->
    when (mediaItem.id) {
        id -> copy(mediaItem)
        else -> mediaItem
    }
})