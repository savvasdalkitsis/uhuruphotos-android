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
package com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapEffect.ErrorLoadingPhotoDetails
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.heatmap.view.implementation.seam.HeatMapEffect.NavigateToPhoto
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.Toaster
import javax.inject.Inject

class HeatMapEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
): EffectHandler<HeatMapEffect> {

    override suspend fun handleEffect(
        effect: HeatMapEffect
    ) {
        when (effect) {
            ErrorLoadingPhotoDetails -> toaster.show(string.error_loading_photo_details)
            NavigateBack -> navigator.navigateBack()
            is NavigateToPhoto -> navigator.navigateTo(with(effect) {
                ExhibitNavigationTarget.name(mediaItem.id, center, scale, mediaItem.isVideo)
            })
        }
    }

}
