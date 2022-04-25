package com.savvasdalkitsis.uhuruphotos.feedpage.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageAction
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageEffect
import com.savvasdalkitsis.uhuruphotos.feedpage.mvflow.FeedPageMutation
import com.savvasdalkitsis.uhuruphotos.feedpage.view.state.FeedPageState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedPageViewModel @Inject constructor(
    feedPageHandler: FeedPageHandler,
) : ViewModel(),
    ActionReceiverHost<FeedPageState, FeedPageEffect, FeedPageAction, FeedPageMutation> {

    override val actionReceiver = ActionReceiver(
        feedPageHandler,
        feedPageReducer(),
        FeedPageState(),
    )
}