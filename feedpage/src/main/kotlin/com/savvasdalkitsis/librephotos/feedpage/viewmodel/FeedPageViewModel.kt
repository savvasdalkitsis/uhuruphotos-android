package com.savvasdalkitsis.librephotos.feedpage.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageMutation
import com.savvasdalkitsis.librephotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FeedPageViewModel @Inject constructor(
    feedPageHandler: FeedPageHandler,
) : ViewModel(),
    ActionReceiverHost<FeedPageState, FeedPageEffect, FeedPageAction, FeedPageMutation> {

    override val initialState = FeedPageState()

    override val actionReceiver = ActionReceiver(
        feedPageHandler,
        feedPageReducer(),
        container(initialState)
    )
}