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
package com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffect.ErrorLoading
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.feature.showroom.view.api.seam.ShowroomEffect.OpenMedia
import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.person.view.api.navigation.PersonNavigationTarget
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.Toaster
import javax.inject.Inject

class ShowroomEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
) : EffectHandler<ShowroomEffect> {

    override suspend fun handleEffect(effect: ShowroomEffect) {
        when (effect) {
            is OpenMedia -> navigate(
                MediaItemPageNavigationTarget.name(
                    effect.id,
                    effect.center,
                    effect.scale,
                    effect.video,
                    effect.mediaSequenceDataSource,
                ),
            )
            NavigateBack -> navigator.navigateBack()
            is NavigateToPerson -> navigate(
                PersonNavigationTarget.name(effect.personId)
            )
            ErrorLoading -> toaster.show(string.error_loading_album)
        }
    }

    private fun navigate(name: String) {
        navigator.navigateTo(name)
    }
}