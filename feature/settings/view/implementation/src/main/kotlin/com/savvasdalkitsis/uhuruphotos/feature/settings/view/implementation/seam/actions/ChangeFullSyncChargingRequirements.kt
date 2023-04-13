package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions

import androidx.work.ExistingPeriodicWorkPolicy
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsEffect
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsEffect.ShowMessage
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.flow.flow

data class ChangeFullSyncChargingRequirements(val requiredCharging: Boolean) : SettingsAction() {
    context(SettingsActionsContext) override fun handle(
        state: SettingsState,
        effect: EffectHandler<SettingsEffect>
    ) = flow<SettingsMutation> {
        settingsUseCase.setFullSyncRequiresCharging(requiredCharging)
        feedWorkScheduler.scheduleFeedRefreshPeriodic(ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE)
        effect.handleEffect(ShowMessage(R.string.feed_sync_charging_changed))
    }

}