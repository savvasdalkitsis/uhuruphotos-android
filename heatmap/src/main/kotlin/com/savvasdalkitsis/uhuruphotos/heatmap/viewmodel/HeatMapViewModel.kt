package com.savvasdalkitsis.uhuruphotos.heatmap.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.heatmap.view.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiver
import com.savvasdalkitsis.uhuruphotos.viewmodel.ActionReceiverHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HeatMapViewModel @Inject constructor(
    heatMapHandler: HeatMapHandler,
): ViewModel(),
    ActionReceiverHost<HeatMapState, HeatMapEffect, HeatMapAction, HeatMapMutation> {

    override val actionReceiver = ActionReceiver<HeatMapState, HeatMapEffect, HeatMapAction, HeatMapMutation>(
        heatMapHandler,
        heatMapReducer(),
        HeatMapState(),
    )

}