package com.savvasdalkitsis.librephotos.home.viewmodel

import com.savvasdalkitsis.librephotos.home.actions.HomeAction
import com.savvasdalkitsis.librephotos.home.effect.HomeEffect
import com.savvasdalkitsis.librephotos.home.mutation.HomeMutation
import com.savvasdalkitsis.librephotos.home.state.HomeState
import com.savvasdalkitsis.librephotos.viewmodel.MVFlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    homeHandler: HomeHandler,
) : MVFlowViewModel<HomeState, HomeAction, HomeMutation, HomeEffect>(
    homeHandler,
    HomeReducer(),
    HomeState(),
    HomeAction.LoadFeed
)
