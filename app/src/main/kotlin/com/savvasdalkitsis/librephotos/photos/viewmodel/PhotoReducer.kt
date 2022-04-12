package com.savvasdalkitsis.librephotos.photos.viewmodel

import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.librephotos.photos.usecase.PhotosUseCase
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.viewmodel.Reducer


fun photoReducer(): Reducer<PhotoState, PhotoMutation> = { state, mutation ->
    when (mutation) {
        is PhotoMutation.ReceivedUrl -> state.copy(
            id = mutation.id,
            isLoading = false,
            lowResUrl = mutation.lowResUrl,
            fullResUrl = mutation.fullResUrl
        )
        is PhotoMutation.ReceivedDetails -> state.copy(
            isFavourite = mutation.details.rating ?: 0 >= PhotosUseCase.FAVOURITES_RATING_THRESHOLD
        )
        PhotoMutation.HideUI -> state.copy(isUIShowing = false)
        PhotoMutation.ShowUI -> state.copy(isUIShowing = true)
    }
}