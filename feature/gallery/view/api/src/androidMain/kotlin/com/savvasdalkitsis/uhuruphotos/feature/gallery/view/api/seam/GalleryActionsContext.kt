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

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryMutation.Loading
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDetails
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GallerySorting
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.api.model.LightboxSequenceDataSource
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.observe
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.set
import com.savvasdalkitsis.uhuruphotos.foundation.result.api.SimpleResult
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.toaster.api.usecase.ToasterUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.properties.Delegates

class GalleryActionsContext(
    val galleryRefresher: suspend (Int) -> SimpleResult,
    val galleryDetailsFlow: (galleryId: Int) -> Flow<GalleryDetails>,
    val shouldRefreshOnLoad: suspend (galleryId: Int) -> Boolean,
    val lightboxSequenceDataSource: (galleryId: Int) -> LightboxSequenceDataSource,
    val initialCollageDisplay: (galleryId: Int) -> CollageDisplay,
    val collageDisplayPersistence: suspend (galleryId: Int, PredefinedCollageDisplay) -> Unit,
    val shouldShowSortingAction: Boolean = true,
    val preferences: Preferences,
    val toaster: ToasterUseCase,
    val navigator: Navigator,
) {

    private val _loading = MutableSharedFlow<GalleryMutation>()
    val loading: Flow<GalleryMutation> get() = _loading
    var galleryId: GalleryId by Delegates.notNull<GalleryId>()
    private val sortingKey get() = "gallerySorting::${galleryId.serializationUniqueId}"

    fun observeSorting(): Flow<GallerySorting> =
        preferences.observe(sortingKey, GallerySorting.default)

    fun setSorting(sorting: GallerySorting) {
        preferences.set(sortingKey, sorting)
    }

    suspend fun refreshGallery() {
        _loading.emit(Loading(true))
        val result = galleryRefresher(galleryId.id)
        if (result.isErr) {
            toaster.show(strings.error_loading_album)
        }
        _loading.emit(Loading(false))
    }
}
