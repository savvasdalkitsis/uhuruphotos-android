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

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.Preference
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffect.ErrorLoading
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GallerySorting
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.EffectHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.properties.Delegates

open class GalleryActionsContext(
    val galleryRefresher: suspend (Int) -> Result<Unit>,
    val galleryDetailsFlow: (galleryId: Int, effect: EffectHandler<GalleryEffect>) -> Flow<GalleryDetails>,
    val galleryDetailsEmptyCheck: suspend (galleryId: Int) -> Boolean,
    val lightboxSequenceDataSource: (galleryId: Int) -> LightboxSequenceDataSource,
    val initialCollageDisplay: (galleryId: Int) -> CollageDisplay,
    val collageDisplayPersistence: suspend (galleryId: Int, PredefinedCollageDisplay) -> Unit,
    val flowSharedPreferences: FlowSharedPreferences,
) {

    val loading = MutableSharedFlow<GalleryMutation>()
    var galleryId by Delegates.notNull<Int>()

    lateinit var sorting: Preference<GallerySorting>

    suspend fun refreshGallery(effect: EffectHandler<GalleryEffect>) {
        loading.emit(Loading(true))
        val result = galleryRefresher(galleryId)
        if (result.isFailure) {
            effect.handleEffect(ErrorLoading)
        }
        loading.emit(Loading(false))
    }
}
