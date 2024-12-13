package com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.ThemeActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui.state.ThemeState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal data class CollageSpacingChanged(val spacing: Int) : ThemeAction() {
    override fun ThemeActionsContext.handle(state: ThemeState): Flow<Mutation<ThemeState>> = flow {
        settingsUIUseCase.setCollageSpacing(spacing)
    }
}