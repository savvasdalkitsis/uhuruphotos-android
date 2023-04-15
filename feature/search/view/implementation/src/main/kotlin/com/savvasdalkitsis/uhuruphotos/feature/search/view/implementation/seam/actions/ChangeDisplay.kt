package com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchEffect
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.seam.SearchMutation.ChangeSearchDisplay
import com.savvasdalkitsis.uhuruphotos.feature.search.view.implementation.ui.state.SearchState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flowOf

data class ChangeDisplay(val display: CollageDisplay) : SearchAction() {
    context(SearchActionsContext) override fun handle(
        state: SearchState,
        effect: EffectHandler<SearchEffect>
    ) = flowOf(ChangeSearchDisplay(display))
}