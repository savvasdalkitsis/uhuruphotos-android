package com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.EditActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.EditMutation
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.EditMutation.SetCropOptionsVisible
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.seam.effects.EditEffect
import com.savvasdalkitsis.uhuruphotos.feature.edit.view.implementation.ui.state.EditState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data object ToggleCropOptions : EditAction() {
    context(EditActionsContext) override fun handle(
        state: EditState,
        effect: EffectHandler<EditEffect>
    ): Flow<Mutation<EditState>> = flowOf(SetCropOptionsVisible(!state.showCropOptions))
}