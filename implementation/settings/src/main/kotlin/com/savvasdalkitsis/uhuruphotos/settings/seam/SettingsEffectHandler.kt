/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.settings.seam

import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.settings.seam.SettingsEffect.ShowMessage
import com.savvasdalkitsis.uhuruphotos.toaster.Toaster
import javax.inject.Inject

internal class SettingsEffectHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
) : EffectHandler<SettingsEffect> {

    override suspend fun handleEffect(
        effect: SettingsEffect,
    ) {
        when(effect) {
            NavigateBack -> navigator.navigateBack()
            is ShowMessage -> toaster.show(effect.message)
        }
    }

}
