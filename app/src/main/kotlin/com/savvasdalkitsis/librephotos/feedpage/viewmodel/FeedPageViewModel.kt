package com.savvasdalkitsis.librephotos.feedpage.viewmodel

import androidx.lifecycle.ViewModel
import coil.annotation.ExperimentalCoilApi
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.librephotos.feedpage.mvflow.FeedPageMutation
import com.savvasdalkitsis.librephotos.feedpage.view.state.FeedPageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@ExperimentalCoilApi
@ExperimentalCoroutinesApi
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