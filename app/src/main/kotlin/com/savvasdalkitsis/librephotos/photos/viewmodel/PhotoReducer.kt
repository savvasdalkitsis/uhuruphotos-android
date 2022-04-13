package com.savvasdalkitsis.librephotos.photos.viewmodel

import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation.*
import com.savvasdalkitsis.librephotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.viewmodel.Reducer


fun photoReducer(): Reducer<PhotoState, PhotoMutation> = { state, mutation ->
    when (mutation) {
        is ReceivedUrl -> state.copy(
            id = mutation.id,
            isLoading = false,
            lowResUrl = mutation.lowResUrl,
            fullResUrl = mutation.fullResUrl
        )
        is ReceivedDetails -> state.copy(
            isFavourite = mutation.details.rating ?: 0 >= PhotosUseCase.FAVOURITES_RATING_THRESHOLD
        )
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
        )
        LoadingDetails -> state.copy(
            isLoading = true,
            showRefresh = false,
        )
        DismissErrorMessage -> state.copy(errorMessage = null)
    }
}