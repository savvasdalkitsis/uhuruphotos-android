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
package com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam

import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.state.ExhibitState
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.state.OriginalFileIconState
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemMetadata
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlin.math.min

sealed class ExhibitMutation(
    mutation: Mutation<ExhibitState>,
) : Mutation<ExhibitState> by mutation {

    object HideUI : ExhibitMutation({
        it.copy(showUI = false)
    })

    object ShowUI : ExhibitMutation({
        it.copy(showUI = true)
    })

    object DismissErrorMessage : ExhibitMutation({
        it.copy(errorMessage = null)
    })

    data class ShowErrorMessage(@StringRes val message: Int) : ExhibitMutation({
        it.copy(
            isLoading = false,
            errorMessage = message,
        )
    })

    object FinishedLoading : ExhibitMutation({
        it.copy(
            isLoading = false,
            showInfoButton = true,
        )
    })

    object Loading : ExhibitMutation({
        it.copy(
            isLoading = true,
        )
    })

    data class LoadingDetails(val id: MediaId<*>) : ExhibitMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(loadingDetails = true)
        }
    })

    data class FinishedLoadingDetails(val id: MediaId<*>) : ExhibitMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(loadingDetails = false)
        }
    })

    object ShowInfo : ExhibitMutation({
        it.copy(infoSheetHidden = false)
    })

    object HideInfo : ExhibitMutation({
        it.copy(infoSheetHidden = true)
    })

    object ShowDeleteConfirmationDialog : ExhibitMutation({
        it.copy(showDeleteConfirmationDialog = true)
    })

    object ShowTrashingConfirmationDialog : ExhibitMutation({
        it.copy(showTrashingConfirmationDialog = true)
    })

    object ShowRestorationConfirmationDialog : ExhibitMutation({
        it.copy(showRestorationConfirmationDialog = true)
    })

    object HideAllConfirmationDialogs : ExhibitMutation({
        it.copy(
            showDeleteConfirmationDialog = false,
            showTrashingConfirmationDialog = false,
            showRestorationConfirmationDialog = false,
        )
    })

    data class ShowShareIcon(val id: MediaId<*>) : ExhibitMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(showShareIcon = true)
        }
    })

    data class ShowUseAsIcon(val id: MediaId<*>) : ExhibitMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(showUseAsIcon = true)
        }
    })

    data class SetOriginalFileIconState(val id: MediaId<*>, val state: OriginalFileIconState) : ExhibitMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(originalFileIconState = state)
        }
    })

    data class ShowMetadata(val id: MediaId<*>, val metadata: MediaItemMetadata) : ExhibitMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(metadata = metadata)
        }
    })

    data class ShowSingleMediaItem(val mediaItemState: SingleMediaItemState) : ExhibitMutation({
        it.copy(
            currentIndex = 0,
            media = listOf(mediaItemState)
        )
    })

    data class ShowMultipleMedia(
        val mediaItemStates: List<SingleMediaItemState>,
        val index: Int,
    ) : ExhibitMutation({
        it.copy(
            currentIndex = index,
            media = mediaItemStates,
        )
    }) {
        override fun toString() = "ShowMultipleMedia [index: $index, size:${mediaItemStates.size}]"
    }

    data class ReceivedDetails(
        val id: MediaId<*>,
        val details: MediaItemDetails,
    ) : ExhibitMutation({
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

    data class ChangeCurrentIndex(val index: Int) : ExhibitMutation({
        it.copy(currentIndex = index)
    })

    data class ShowMediaItemFavourite(
        val id: MediaId<*>,
        val favourite: Boolean,
    ) : ExhibitMutation({
        it.copyItem(id) { photoState ->
            photoState.copy(isFavourite = favourite)
        }
    })

    data class RemoveMediaItemFromSource(val id: MediaId<*>) : ExhibitMutation({
        val removed = it.copy(
            media = it.media.filter { photoState -> photoState.id != id },
        )
        removed.copy(
            currentIndex = min(it.currentIndex, removed.media.size - 1)
        )
    })

    object ShowRestoreButton : ExhibitMutation({
        it.copy(showRestoreButton = true)
    })
}

private fun ExhibitState.copyItem(
    id: MediaId<*>,
    copy: (SingleMediaItemState) -> SingleMediaItemState
): ExhibitState = copy(media = media.map { mediaItem ->
    when (mediaItem.id) {
        id -> copy(mediaItem)
        else -> mediaItem
    }
})