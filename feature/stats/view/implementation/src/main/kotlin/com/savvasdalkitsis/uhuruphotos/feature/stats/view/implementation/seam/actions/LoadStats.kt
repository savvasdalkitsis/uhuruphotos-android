/*
Copyright 2023 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.feed.domain.api.model.FeedFetchType
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.seam.StatsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.seam.StatsMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.seam.StatsMutation.ShowMediaPerDayOfMonth
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.seam.StatsMutation.ShowMediaPerDayOfWeek
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.seam.StatsMutation.ShowMediaPerMonth
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.seam.StatsMutation.ShowMediaPerYear
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.ui.state.StatsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.onStart

data object LoadStats : StatsAction() {

    context(StatsActionsContext)
    override fun handle(state: StatsState): Flow<Mutation<StatsState>> = channelFlow {
        val media = feedUseCase.getFeed(FeedFetchType.ONLY_WITH_DATES)
            .flatMap { it.mediaItems }
        with(statsUseCase) {
            listOf(
                async { trySend(ShowMediaPerYear(media.breakdownByYear())) },
                async { trySend(ShowMediaPerMonth(media.breakdownByMonth())) },
                async { trySend(ShowMediaPerDayOfMonth(media.breakdownByDayOfMonth())) },
                async { trySend(ShowMediaPerDayOfWeek(media.breakdownByDayOfWeek())) },
            ).awaitAll()
//            media.timeline()
//                .onSuccess {
//                    emit(ShowTimeline(it))
//                }.onFailure {
//                    emit(ShowTimeline(emptyList()))
//                }
        }
    }.onStart { Loading }

}