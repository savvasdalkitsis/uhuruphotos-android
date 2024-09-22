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

import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.api.navigation.AutoAlbumNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.AutoAlbumActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.action.AutoAlbumAction
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.seam.action.SetAlbumId
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.ui.state.AutoAlbumCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.album.auto.view.implementation.ui.state.AutoAlbumState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel.NavigationViewModel
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

typealias AutoAlbumCompositeState = Pair<GalleryState, AutoAlbumState>
typealias AutoAlbumCompositeAction = Either<GalleryAction, AutoAlbumAction>

@HiltViewModel
internal class AutoAlbumViewModel @Inject constructor(
    autoAlbumActionsContext: AutoAlbumActionsContext,
) : NavigationViewModel<AutoAlbumCompositeState, AutoAlbumCompositeAction, AutoAlbumNavigationRoute>(
    CompositeActionHandler(
        ActionHandlerWithContext(autoAlbumActionsContext.galleryActionsContext),
        ActionHandlerWithContext(autoAlbumActionsContext),
    ),
    GalleryState(collageState = CollageState(collageDisplayState = AutoAlbumCollageDisplayState)) to AutoAlbumState()
) {

    override fun onRouteSet(route: AutoAlbumNavigationRoute) {
        val id = route.albumId
        action(Either.Left(LoadCollage(GalleryId(id, "auto:$id"))))
        action(Either.Right(SetAlbumId(id)))
    }
}