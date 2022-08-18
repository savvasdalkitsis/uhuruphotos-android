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
package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.settings.view.api.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashEffect.NavigateToAppSettings
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import javax.inject.Inject

class TrashEffectsHandler @Inject constructor(
    private val navigator: Navigator,
): EffectHandler<TrashEffect> {

    override suspend fun handleEffect(effect: TrashEffect) {
        when (effect) {
            NavigateToAppSettings -> navigator.navigateTo(SettingsNavigationTarget.name)
        }
    }

}
