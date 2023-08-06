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
package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.viewmodel

import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.api.HiddenPhotosNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAlbumPageActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private typealias HiddenPhotosCompositeState = Pair<GalleryState, HiddenPhotosState>
private typealias HiddenPhotosCompositeAction = Either<GalleryAction, HiddenPhotosAction>

@HiltViewModel
internal class HiddenPhotosViewModel @Inject constructor(
    hiddenPhotosAlbumPageActionsContext: HiddenPhotosAlbumPageActionsContext,
    hiddenPhotosActionsContext: HiddenPhotosActionsContext,
) : NavigationViewModel<HiddenPhotosCompositeState, HiddenPhotosCompositeAction, HiddenPhotosNavigationRoute>(
    CompositeActionHandler(
        ActionHandlerWithContext(hiddenPhotosAlbumPageActionsContext.galleryActionsContext),
        ActionHandlerWithContext(hiddenPhotosActionsContext),
    ),
    GalleryState(collageState = CollageState()) to HiddenPhotosState()
) {

    override fun onRouteSet(route: HiddenPhotosNavigationRoute) {
        action(Either.Left(LoadCollage(GalleryId(0, "hidden"))))
        action(Either.Right(Load))
    }
}