package com.savvasdalkitsis.librephotos.feed.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedPageMutation
import com.savvasdalkitsis.librephotos.feed.view.state.FeedPageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class FeedPageViewModel @Inject constructor(
    feedPageHandler: FeedPageHandler,
) : ViewModel(),
    com.savvasdalkitsis.librephotos.viewmodel.ActionReceiverHost<FeedPageState, FeedPageEffect, FeedPageAction, FeedPageMutation> {

    override val initialState = FeedPageState()

    override val actionReceiver = com.savvasdalkitsis.librephotos.viewmodel.ActionReceiver(
        feedPageHandler,
        feedPageReducer(),
        container(initialState)
    )
}