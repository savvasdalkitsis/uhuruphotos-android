package com.savvasdalkitsis.librephotos.photos.viewmodel

import com.savvasdalkitsis.librephotos.photos.mvflow.PhotoMutation
import com.savvasdalkitsis.librephotos.photos.view.state.PhotoState
import com.savvasdalkitsis.librephotos.viewmodel.Reducer


fun photoReducer(): Reducer<PhotoState, PhotoMutation> = { state, mutation ->
    when (mutation) {
        is PhotoMutation.ReceivedUrl -> state.copy(isLoading = false, lowResUrl = mutation.lowResUrl, fullResUrl = mutation.fullResUrl)
        is PhotoMutation.ReceivedDetails -> state.copy()
    }
}