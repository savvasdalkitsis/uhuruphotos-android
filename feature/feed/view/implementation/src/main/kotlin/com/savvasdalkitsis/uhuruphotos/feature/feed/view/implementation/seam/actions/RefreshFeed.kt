package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

object RefreshFeed : FeedAction() {
    context(FeedActionsContext) override fun handle(
        state: FeedState,
        effect: EffectHandler<FeedEffect>
    ) = flow {
        emit(StartRefreshing)
        feedUseCase.refreshFeed(shallow = true)
        delay(200)
        emit(StopRefreshing)
    }

}