package com.savvasdalkitsis.uhuruphotos.home.viewmodel

import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeMutation.Loading
import com.savvasdalkitsis.uhuruphotos.home.view.state.HomeState
import com.savvasdalkitsis.uhuruphotos.viewmodel.Reducer

fun homeReducer() : Reducer<HomeState, HomeMutation> = { state, mutation ->
    when (mutation) {
        Loading -> state.copy(isLoading = true)
    }
}
