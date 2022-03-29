package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation.Loading
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import net.pedroloureiro.mvflow.Reducer

class HomeReducer : Reducer<HomeState, HomeMutation> {

    override fun invoke(
        state: HomeState,
        mutation: HomeMutation
    ): HomeState = when (mutation) {
        Loading -> state.copy(isLoading = true)
    }

}
