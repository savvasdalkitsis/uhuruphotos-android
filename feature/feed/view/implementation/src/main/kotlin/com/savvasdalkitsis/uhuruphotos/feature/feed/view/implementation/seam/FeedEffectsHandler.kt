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
package com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.model.ExhibitSequenceDataSource.Feed
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.api.navigation.ExhibitNavigationTarget
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.DownloadingFiles
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.OpenExhibit
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.SharePhotos
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.Vibrate
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.Toaster
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import javax.inject.Inject

internal class FeedEffectsHandler @Inject constructor(
    private val navigator: Navigator,
    private val shareUseCase: ShareUseCase,
    private val toaster: Toaster,
    private val uiUseCase: UiUseCase,
) : EffectHandler<FeedEffect> {

    override suspend fun handleEffect(effect: FeedEffect) = when (effect) {
        is OpenExhibit -> with(effect) {
            navigateTo(ExhibitNavigationTarget.name(id, center, scale, isVideo, Feed))
        }
        is SharePhotos -> {
            toaster.show(string.downloading_photos_sharing)
            shareUseCase.shareMultiple(effect.selectedMediaItem.mapNotNull {
                it.fullResUri
            })
        }
        Vibrate -> uiUseCase.performLongPressHaptic()
        DownloadingFiles -> toaster.show(string.downloading_original_files)
    }

    private fun navigateTo(target: String) {
        navigator.navigateTo(target)
    }
}