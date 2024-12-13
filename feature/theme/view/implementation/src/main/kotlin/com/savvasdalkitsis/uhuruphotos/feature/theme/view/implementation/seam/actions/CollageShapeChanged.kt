package com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.ThemeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui.state.ThemeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollageShape
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class CollageShapeChanged(val collageShape: CollageShape) : ThemeAction() {
    override fun ThemeActionsContext.handle(state: ThemeState): Flow<Mutation<ThemeState>> = flow {
        settingsUIUseCase.setCollageShape(collageShape)
    }
}