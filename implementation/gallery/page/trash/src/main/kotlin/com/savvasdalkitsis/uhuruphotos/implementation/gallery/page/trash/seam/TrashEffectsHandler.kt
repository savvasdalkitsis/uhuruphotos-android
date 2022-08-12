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
package com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.trash.seam

import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.api.settings.navigation.SettingsNavigationTarget
import com.savvasdalkitsis.uhuruphotos.implementation.gallery.page.trash.seam.TrashEffect.NavigateToAppSettings
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
