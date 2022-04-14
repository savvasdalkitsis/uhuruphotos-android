package com.savvasdalkitsis.librephotos.photos.viewmodel

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import com.google.android.gms.maps.model.LatLng
import com.savvasdalkitsis.librephotos.infrastructure.date.DateDisplayer
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation.*
import com.savvasdalkitsis.librephotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.viewmodel.Reducer
import javax.inject.Inject

@ExperimentalMaterialApi
class PhotoReducer @Inject constructor(
    private val dateDisplayer: DateDisplayer
) : Reducer<PhotoState, PhotoMutation> {

    override fun invoke(
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
                showRefresh = true,
                showInfoButton = true,
                dateAndTime = dateDisplayer.dateTimeString(timestamp),
                location = location ?: "",
                gps = gpsLat?.toDoubleOrNull()?.let { lat ->
                    gpsLon?.toDoubleOrNull()?.let { lon ->
                        LatLng(lat, lon)
                    }
                }
            )
        }
        HideUI -> state.copy(showUI = false)
        ShowUI -> state.copy(showUI = true)
        is ShowErrorMessage -> state.copy(
            isLoading = false,
            showRefresh = true,
            errorMessage = mutation.message,
        )
        FinishedLoadingDetails -> state.copy(
            isLoading = false,
            showRefresh = true,
            showInfoButton = true,
        )
        LoadingDetails -> state.copy(
            isLoading = true,
            showRefresh = false,
        )
        DismissErrorMessage -> state.copy(errorMessage = null)
        ShowInfo -> state.copy(infoSheetState = ModalBottomSheetValue.HalfExpanded)
        HideInfo -> state.copy(infoSheetState = ModalBottomSheetValue.Hidden)
    }
}