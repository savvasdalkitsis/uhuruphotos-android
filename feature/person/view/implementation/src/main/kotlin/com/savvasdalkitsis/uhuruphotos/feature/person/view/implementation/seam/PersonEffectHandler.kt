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
package com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.PersonResults
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.person.view.implementation.seam.PersonEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import javax.inject.Inject

class PersonEffectHandler @Inject constructor(
    private val navigator: Navigator,
) : EffectHandler<PersonEffect> {

    override suspend fun handleEffect(effect: PersonEffect) {
        when (effect) {
            NavigateBack -> navigator.navigateBack()
            is OpenLightbox -> navigator.navigateTo(with(effect) {
                LightboxNavigationTarget.name(
                    id,
                    center,
                    scale,
                    video,
                    PersonResults(person.id)
                )
            })
        }
    }

}
