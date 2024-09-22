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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.ClusterState
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable.SelectionMode
import kotlinx.coroutines.flow.flow

data class ClusterSelectionClicked(val clusterState: ClusterState) : FeedAction() {
    context(FeedActionsContext) override fun handle(
        state: FeedState
    ) = flow<FeedMutation> {
        val cels = clusterState.cels
        uiUseCase.performLongPressHaptic()
        if (cels.all { it.selectionMode == SelectionMode.SELECTED }) {
            cels.forEach { it.deselect() }
        } else {
            cels.forEach { it.select() }
        }
    }
}
