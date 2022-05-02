package com.savvasdalkitsis.uhuruphotos.photos.viewmodel

import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.uhuruphotos.infrastructure.date.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.photos.model.latLng
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.uhuruphotos.photos.mvflow.PhotoMutation.*
import com.savvasdalkitsis.uhuruphotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.uhuruphotos.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer
import javax.inject.Inject

class PhotoReducer @Inject constructor(
    private val dateDisplayer: DateDisplayer,
) : Reducer<PhotoState, PhotoMutation> {

    override suspend fun invoke(
        state: PhotoState,
        mutation: PhotoMutation
    ): PhotoState = when (mutation) {
        is ReceivedUrl -> state.copy(
            id = mutation.id,
            isLoading = false,
            lowResUrl = mutation.lowResUrl,
            fullResUrl = mutation.fullResUrl
        )
        is ReceivedDetails -> with(mutation.details) {
            state.copy(
                isFavourite = rating ?: 0 >= PhotosUseCase.FAVOURITES_RATING_THRESHOLD,
                isVideo = video == true,
                showRefresh = true,
                showInfoButton = true,
                dateAndTime = dateDisplayer.dateTimeString(timestamp),
                location = location ?: "",
                gps = latLng,
                peopleInPhoto = mutation.peopleInPhoto
            )
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
        ShowShareIcon -> state.copy(showShareIcon = true)
    }
}