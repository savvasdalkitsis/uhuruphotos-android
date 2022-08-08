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
package com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam

import com.savvasdalkitsis.uhuruphotos.api.media.page.navigation.MediaItemPageNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.api.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam.HeatMapEffect.ErrorLoadingPhotoDetails
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam.HeatMapEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.implementation.heatmap.seam.HeatMapEffect.NavigateToPhoto
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
                MediaItemPageNavigationTarget.name(mediaItem.id, center, scale, mediaItem.isVideo)
            })
        }
    }

}
