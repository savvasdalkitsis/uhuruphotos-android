package com.savvasdalkitsis.librephotos.feed.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    feedHandler: FeedHandler,
) : ViewModel(), ActionReceiverHost<FeedPageState, FeedEffect, FeedAction, FeedMutation> {

    override val initialState = FeedPageState()

    override val actionReceiver = ActionReceiver(
        feedHandler,
        feedReducer(),
        container(initialState)
    )
}