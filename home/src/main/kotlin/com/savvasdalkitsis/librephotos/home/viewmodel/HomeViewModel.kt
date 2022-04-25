package com.savvasdalkitsis.librephotos.home.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.home.mvflow.HomeAction
import com.savvasdalkitsis.librephotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.librephotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.librephotos.home.view.state.HomeState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    homeHandler: HomeHandler,
) : ViewModel(),
    ActionReceiverHost<HomeState, HomeEffect, HomeAction, HomeMutation> {

    override val actionReceiver = ActionReceiver(
        homeHandler,
        homeReducer(),
        HomeState(),
    )
}