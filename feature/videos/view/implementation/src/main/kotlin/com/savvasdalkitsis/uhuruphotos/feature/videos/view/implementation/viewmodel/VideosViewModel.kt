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
package com.savvasdalkitsis.uhuruphotos.feature.videos.view.implementation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.videos.view.api.navigation.VideosNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.videos.view.implementation.seam.VideosActionsContext
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import dagger.hilt.android.lifecycle.HiltViewModel
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.videos
import javax.inject.Inject

@HiltViewModel
internal class VideosViewModel @Inject constructor(
    actionsContext: VideosActionsContext,
    handle: SavedStateHandle,
) : NavigationViewModel<GalleryState, GalleryAction, VideosNavigationRoute>(
    ActionHandlerWithContext(actionsContext.galleryActionsContext),
    GalleryState(title = Title.Resource(string.videos)),
    handle,
) {

    override fun onRouteSet(route: VideosNavigationRoute) {
        action(LoadCollage(GalleryId(0, "videos")))
    }
}