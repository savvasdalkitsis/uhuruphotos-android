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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.actions

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsEffect
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsEffect.*
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.seam.SettingsMutation
import com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui.state.SettingsState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import kotlinx.coroutines.flow.flow

data class ChangeFullSyncNetworkRequirements(val networkType: NetworkType) : SettingsAction() {
    context(SettingsActionsContext) override fun handle(
        state: SettingsState,
        effect: EffectHandler<SettingsEffect>
    ) = flow<SettingsMutation> {
        settingsUseCase.setFullSyncNetworkRequirements(networkType)
        feedWorkScheduler.scheduleFeedRefreshPeriodic(ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE)
        effect.handleEffect(ShowMessage(R.string.feed_sync_network_changed))
    }
}
