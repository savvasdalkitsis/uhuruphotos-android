package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapEffect
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapMutation.HideLocalStoragePermissionRequest
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.ui.state.HeatMapState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

object NeverAskForLocalMediaAccessPermissionRequest : HeatMapAction() {
    context(HeatMapActionsContext) override fun handle(
        state: HeatMapState,
        effect: EffectHandler<HeatMapEffect>
    ) = flow {
        emit(HideLocalStoragePermissionRequest)
        settingsUseCase.setShowBannerAskingForLocalMediaPermissionsOnHeatmap(false)
    }
}