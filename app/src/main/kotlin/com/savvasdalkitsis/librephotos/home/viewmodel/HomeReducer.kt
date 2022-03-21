package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation.*
import com.savvasdalkitsis.librephotos.home.state.HomeState
import net.pedroloureiro.mvflow.Reducer

class HomeReducer : Reducer<HomeState, HomeMutation> {

    override fun invoke(
        state: HomeState,
        mutation: HomeMutation
    ): HomeState = when (mutation) {
        is Loading -> state.copy(isLoading = true)
        is PartiallyLoaded -> state.copy(isLoading = true, feedState = mutation.feedState)
        is Loaded -> state.copy(isLoading = false, feedState = mutation.feedState)
    }

}
