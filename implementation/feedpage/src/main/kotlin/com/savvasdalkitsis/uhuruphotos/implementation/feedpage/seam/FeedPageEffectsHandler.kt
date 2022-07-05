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
package com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam

import com.savvasdalkitsis.uhuruphotos.api.navigation.Navigator
import com.savvasdalkitsis.uhuruphotos.api.photos.model.PhotoSequenceDataSource.AllPhotos
import com.savvasdalkitsis.uhuruphotos.api.photos.navigation.PhotoNavigationTarget
import com.savvasdalkitsis.uhuruphotos.api.seam.EffectHandler
import com.savvasdalkitsis.uhuruphotos.api.share.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.api.strings.R
import com.savvasdalkitsis.uhuruphotos.api.toaster.Toaster
import com.savvasdalkitsis.uhuruphotos.api.ui.usecase.UiUseCase
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffect.OpenPhotoDetails
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffect.SharePhotos
import com.savvasdalkitsis.uhuruphotos.implementation.feedpage.seam.FeedPageEffect.Vibrate
import javax.inject.Inject

internal class FeedPageEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val shareUseCase: ShareUseCase,
    private val toaster: Toaster,
    private val uiUseCase: UiUseCase,
) : EffectHandler<FeedPageEffect> {

    override suspend fun handleEffect(effect: FeedPageEffect) = when (effect) {
        is OpenPhotoDetails -> with(effect) {
            navigateTo(PhotoNavigationTarget.name(id, center, scale, isVideo, AllPhotos))
        }
        is SharePhotos -> {
            toaster.show(R.string.downloading_photos_sharing)
            shareUseCase.shareMultiple(effect.selectedPhotos.mapNotNull {
                it.fullResUrl
            })
        }
        Vibrate -> uiUseCase.performLongPressHaptic()
    }

    private fun navigateTo(target: String) {
        navigator.navigateTo(target)
    }
}