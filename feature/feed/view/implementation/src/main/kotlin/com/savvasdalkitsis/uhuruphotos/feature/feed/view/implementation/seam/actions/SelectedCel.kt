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

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.effects.FeedEffect
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.effects.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedMutation
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.effects.Vibrate
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.ui.state.FeedState
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItemSelectionMode
import com.savvasdalkitsis.uhuruphotos.feature.media.common.view.api.ui.state.CelState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class SelectedCel(
    val celState: CelState,
) : FeedAction() {
    context(FeedActionsContext) override fun handle(
        state: FeedState,
        effect: EffectHandler<FeedEffect>
    ) = flow<FeedMutation> {
        when {
            state.selectedCelCount == 0 -> effect.handleEffect(
                OpenLightbox(celState.mediaItem.id)
            )
            celState.selectionMode == MediaItemSelectionMode.SELECTED -> {
                effect.handleEffect(Vibrate)
                celState.deselect()
            }
            else -> {
                effect.handleEffect(Vibrate)
                celState.select()
            }
        }
    }

}
