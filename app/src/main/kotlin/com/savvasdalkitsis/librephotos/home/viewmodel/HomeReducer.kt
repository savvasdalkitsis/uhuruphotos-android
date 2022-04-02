package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation.Loading
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import com.savvasdalkitsis.librephotos.viewmodel.Reducer

fun homeReducer() : Reducer<HomeState, HomeMutation> = { state, mutation ->
    when (mutation) {
        Loading -> state.copy(isLoading = true)
    }
}
