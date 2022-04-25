package com.savvasdalkitsis.uhuruphotos.home.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeAction
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeEffect
import com.savvasdalkitsis.uhuruphotos.home.mvflow.HomeMutation
import com.savvasdalkitsis.uhuruphotos.home.view.state.HomeState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
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