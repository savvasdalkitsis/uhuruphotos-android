package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapEffect
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.foundation.map.api.model.LatLon
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.flow

data class CameraViewPortChanged(val newBoundsChecker: suspend (LatLon) -> Boolean) : HeatMapAction() {

    private var updateVisibleMapContentJob: Deferred<HeatMapMutation.UpdateVisibleMapContent>? = null

    context(HeatMapActionsContext) override fun handle(
        state: HeatMapState,
        effect: EffectHandler<HeatMapEffect>
    ) = flow {
        updateVisibleMapContentJob?.cancel()
        boundsChecker = newBoundsChecker
        updateVisibleMapContentJob = CoroutineScope(currentCoroutineContext()).async { updateDisplay(state.allMedia) }
        updateVisibleMapContentJob?.await()?.let {
            emit(it)
        }
    }
}