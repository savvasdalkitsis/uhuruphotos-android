/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.undated.view.implementation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.undated.view.api.navigation.UndatedNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.undated.view.implementation.seam.UndatedActionsContext
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import dagger.hilt.android.lifecycle.HiltViewModel
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.media_without_date
import javax.inject.Inject

@HiltViewModel
internal class UndatedViewModel @Inject constructor(
    actionsContext: UndatedActionsContext,
    handle: SavedStateHandle,
) : NavigationViewModel<GalleryState, GalleryAction, UndatedNavigationRoute>(
    ActionHandlerWithContext(actionsContext.galleryActionsContext),
    GalleryState(title = Title.Resource(string.media_without_date)),
    handle,
) {

    override fun onRouteSet(route: UndatedNavigationRoute) {
        action(LoadCollage(GalleryId(0, "undated")))
    }
}