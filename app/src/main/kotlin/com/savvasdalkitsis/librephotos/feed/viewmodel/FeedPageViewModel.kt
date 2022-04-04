package com.savvasdalkitsis.librephotos.feed.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageMutation
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class FeedPageViewModel @Inject constructor(
    feedPageHandler: FeedPageHandler,
) : ViewModel(), ActionReceiverHost<FeedPageState, FeedPageEffect, FeedPageAction, FeedPageMutation> {

    override val initialState = FeedPageState()

    override val actionReceiver = ActionReceiver(
        feedPageHandler,
        feedPageReducer(),
        container(initialState)
    )
}