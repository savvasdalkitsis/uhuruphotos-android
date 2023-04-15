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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam

import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.ErrorLoading
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.NavigateBack
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.NavigateToPerson
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.OpenLightbox
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.navigation.LightboxNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.person.view.api.navigation.PersonNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.Toaster
import javax.inject.Inject

class GalleryEffectHandler @Inject constructor(
    private val navigator: Navigator,
    private val toaster: Toaster,
) : EffectHandler<GalleryEffect> {

    override suspend fun handleEffect(effect: GalleryEffect) {
        when (effect) {
            is OpenLightbox -> {
                navigator.navigateTo(
                    LightboxNavigationRoute(
                        id = effect.id,
                        isVideo = effect.video,
                        lightboxSequenceDataSource = effect.lightboxSequenceDataSource,
                    )
                )
            }
            NavigateBack -> navigator.navigateBack()
            is NavigateToPerson -> {
                navigator.navigateTo(
                    PersonNavigationRoute(effect.personId)
                )
            }
            ErrorLoading -> toaster.show(string.error_loading_album)
        }
    }

}