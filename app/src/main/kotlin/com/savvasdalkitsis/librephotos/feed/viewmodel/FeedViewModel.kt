package com.savvasdalkitsis.librephotos.feed.viewmodel

import com.savvasdalkitsis.librephotos.feed.mvflow.FeedAction
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedEffect
import com.savvasdalkitsis.librephotos.feed.mvflow.FeedMutation
import com.savvasdalkitsis.librephotos.feed.view.state.FeedState
import com.savvasdalkitsis.librephotos.viewmodel.MVFlowViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    feedHandler: FeedHandler,
) : MVFlowViewModel<FeedState, FeedAction, FeedMutation, FeedEffect>(
    feedHandler,
    FeedReducer(),
    FeedState(),
    FeedAction.LoadFeed,
)