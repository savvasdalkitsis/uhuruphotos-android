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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.seam

import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.api.seam.Mutation
import com.savvasdalkitsis.uhuruphotos.api.db.photos.PhotoDetails
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.Person
import com.savvasdalkitsis.uhuruphotos.implementation.photos.model.latLng
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.SinglePhotoState
import kotlin.math.min

sealed class PhotoMutation(
    mutation: Mutation<PhotoState>,
) : Mutation<PhotoState> by mutation {

    object HideUI : PhotoMutation({
        it.copy(showUI = false)
    })

    object ShowUI : PhotoMutation({
        it.copy(showUI = true)
    })

    object DismissErrorMessage : PhotoMutation({
        it.copy(errorMessage = null)
    })

    data class ShowErrorMessage(@StringRes val message: Int) : PhotoMutation({
        it.copy(
            isLoading = false,
            showRefresh = true,
            errorMessage = message,
        )
    })

    object FinishedLoading : PhotoMutation({
        it.copy(
            isLoading = false,
            showRefresh = true,
            showInfoButton = true,
        )
    })

    object Loading : PhotoMutation({
        it.copy(
            isLoading = true,
            showRefresh = false,
        )
    })

    object ShowInfo : PhotoMutation({
        it.copy(infoSheetHidden = false)
    })

    object HideInfo : PhotoMutation({
        it.copy(infoSheetHidden = true)
    })

    object ShowDeletionConfirmationDialog : PhotoMutation({
        it.copy(showPhotoDeletionConfirmationDialog = true)
    })

    object HideDeletionConfirmationDialog : PhotoMutation({
        it.copy(showPhotoDeletionConfirmationDialog = false)
    })

    data class ShowShareIcon(val id: String) : PhotoMutation({
        it.copyPhoto(id) { photoState ->
            photoState.copy(showShareIcon = true)
        }
    })

    data class ShowSinglePhoto(val photoState: SinglePhotoState) : PhotoMutation({
        it.copy(
            currentIndex = 0,
            photos = listOf(photoState)
        )
    })

    data class ShowMultiplePhotos(
        val photoStates: List<SinglePhotoState>,
        val index: Int,
    ) : PhotoMutation({
        it.copy(
            currentIndex = index,
            photos = photoStates,
        )
    })

    data class ReceivedDetails(
        val details: PhotoDetails,
        val peopleInPhoto: List<Person>,
        val favouriteThreshold: Int?,
        val formattedDateAndTime: String,
    ) : PhotoMutation({
        with(details) {
            it.copyPhoto(imageHash) { photoState ->
                photoState.copy(
                    isFavourite = favouriteThreshold != null
                            && (rating ?: 0) >= favouriteThreshold,
                    isVideo = video == true,
                    dateAndTime = formattedDateAndTime,
                    location = location ?: "",
                    gps = latLng,
                    peopleInPhoto = peopleInPhoto
                )
            }
        }
    })

    data class ChangeCurrentIndex(val index: Int) : PhotoMutation({
        it.copy(currentIndex = index)
    })

    data class ShowPhotoFavourite(
        val id: String,
        val favourite: Boolean,
    ) : PhotoMutation({
        it.copyPhoto(id) { photoState ->
            photoState.copy(isFavourite = favourite)
        }
    })

    data class RemovePhotoFromSource(val id: String) : PhotoMutation({
        val removed = it.copy(
            photos = it.photos.filter { photoState -> photoState.id != id },
        )
        removed.copy(
            currentIndex = min(it.currentIndex, removed.photos.size - 1)
        )
    })
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