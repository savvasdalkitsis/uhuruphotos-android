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

import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.DownloadingFiles
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.OpenMemoryLightbox
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.Share
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.ShowErrorDeletingMedia
import com.savvasdalkitsis.uhuruphotos.feature.feed.view.implementation.seam.FeedEffect.Vibrate
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Feed
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource.Memory
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaId
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.share.api.usecase.ShareUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import javax.inject.Inject

internal class FeedEffectHandler @Inject constructor(
    private val navigator: Navigator,
    private val shareUseCase: ShareUseCase,
    private val toasterUseCase: ToasterUseCase,
    private val uiUseCase: UiUseCase,
) : EffectHandler<FeedEffect> {

    override suspend fun handleEffect(effect: FeedEffect) = when (effect) {
        is OpenLightbox -> openLightBox(effect.id, Feed)
        is Share -> {
            toasterUseCase.show(string.downloading_photos_sharing)
            shareUseCase.shareMultiple(effect.selectedCels.mapNotNull {
                it.mediaItem.fullResUri
            })
        }
        Vibrate -> uiUseCase.performLongPressHaptic()
        DownloadingFiles -> toasterUseCase.show(string.downloading_original_files)
        is OpenMemoryLightbox -> openLightBox(effect.id, Memory(effect.yearsAgo))
        ShowErrorDeletingMedia -> toasterUseCase.show(string.error_deleting_media)
    }

    private fun openLightBox(id: MediaId<*>, sequenceDataSource: LightboxSequenceDataSource) {
        navigator.navigateTo(LightboxNavigationRoute(id, sequenceDataSource, showMediaSyncState = true))
    }

}