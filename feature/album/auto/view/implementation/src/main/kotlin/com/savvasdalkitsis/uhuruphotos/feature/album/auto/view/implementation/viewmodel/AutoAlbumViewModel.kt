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
package com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.api.navigation.AutoAlbumNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.AutoAlbumActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.state.AutoAlbumCollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class AutoAlbumViewModel @Inject constructor(
    autoAlbumActionsContext: AutoAlbumActionsContext,
    effectHandler: GalleryEffectHandler,
) : ViewModel(), HasActionableState<GalleryState, GalleryAction> by Seam(
    ActionHandlerWithContext(autoAlbumActionsContext),
    effectHandler,
    GalleryState(collageState = CollageState(collageDisplay = AutoAlbumCollageDisplay))
), HasInitializer<AutoAlbumNavigationRoute> {
    override suspend fun initialize(initializerData: AutoAlbumNavigationRoute) {
        val id = initializerData.albumId
        action(LoadCollage(GalleryId(id, "auto:$id")))
    }
}