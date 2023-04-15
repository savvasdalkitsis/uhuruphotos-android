package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.Cluster
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation.*
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class ClusterRefreshClicked(val cluster: Cluster) : FeedAction() {
    context(FeedActionsContext) override fun handle(
        state: FeedState,
        effect: EffectHandler<FeedEffect>
    ) = flow {
        emit(StartRefreshing)
        feedUseCase.refreshCluster(cluster.id)
        emit(StopRefreshing)
    }
}