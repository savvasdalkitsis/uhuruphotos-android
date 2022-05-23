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
package com.savvasdalkitsis.uhuruphotos.photos.viewmodel

import com.savvasdalkitsis.uhuruphotos.infrastructure.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.photos.model.latLng
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoMutation.*
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.photos.view.state.SinglePhotoState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer
import javax.inject.Inject
import kotlin.math.min

class PhotoReducer @Inject constructor(
    private val dateDisplayer: DateDisplayer,
) : Reducer<PhotoState, PhotoMutation> {

    override suspend fun invoke(
        state: PhotoState,
        mutation: PhotoMutation
    ): PhotoState = when (mutation) {
        is ShowSinglePhoto -> state.copy(
            currentIndex = 0,
            photos = listOf(mutation.photoState)
        )
        is ShowMultiplePhotos -> state.copy(
            currentIndex = mutation.index,
            photos = mutation.photoStates,
        )
        is ChangeCurrentIndex -> state.copy(currentIndex = mutation.index)
        is ReceivedDetails -> with(mutation.details) {
            state.copyPhoto(imageHash) {
                val threshold = mutation.favouriteThreshold
                it.copy(
                    isFavourite = threshold != null
                            && (rating ?: 0) >= threshold,
                    isVideo = video == true,
                    dateAndTime = dateDisplayer.dateTimeString(timestamp),
                    location = location ?: "",
                    gps = latLng,
                    peopleInPhoto = mutation.peopleInPhoto
                )
            }
        }
        HideUI -> state.copy(showUI = false)
        ShowUI -> state.copy(showUI = true)
        is ShowErrorMessage -> state.copy(
            isLoading = false,
            showRefresh = true,
            errorMessage = mutation.message,
        )
        FinishedLoading -> state.copy(
            isLoading = false,
            showRefresh = true,
            showInfoButton = true,
        )
        Loading -> state.copy(
            isLoading = true,
            showRefresh = false,
        )
        DismissErrorMessage -> state.copy(errorMessage = null)
        ShowInfo -> state.copy(infoSheetHidden = false)
        HideInfo -> state.copy(infoSheetHidden = true)
        ShowDeletionConfirmationDialog -> state.copy(showPhotoDeletionConfirmationDialog = true)
        HideDeletionConfirmationDialog -> state.copy(showPhotoDeletionConfirmationDialog = false)
        is ShowShareIcon -> state.copyPhoto(mutation.id) {
            it.copy(showShareIcon = true)
        }
        is ShowPhotoFavourite -> state.copyPhoto(mutation.id) {
            it.copy(isFavourite = mutation.favourite)
        }
        is RemovePhotoFromSource -> {
            val removed = state.copy(
                photos = state.photos.filter { it.id != mutation.id },
            )
            removed.copy(
                currentIndex = min(state.currentIndex, removed.photos.size - 1)
            )
        }
    }

    private fun PhotoState.copyPhoto(
        imageHash: String,
        copy: (SinglePhotoState) -> SinglePhotoState
    ): PhotoState = copy(photos = photos.map { photo ->
        when (photo.id) {
            imageHash -> copy(photo)
            else -> photo
        }
    })
}