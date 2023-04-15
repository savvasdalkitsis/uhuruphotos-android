package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsEffect
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.flow

data class ChangeAnimateVideoThumbnails(val animate: Boolean) : SettingsAction() {
    context(SettingsActionsContext) override fun handle(
        state: SettingsState,
        effect: EffectHandler<SettingsEffect>
    ) = flow<SettingsMutation> {
        settingsUseCase.setAnimateVideoThumbnails(animate)
    }
}