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
package com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.local.view.api.navigation.LocalAlbumNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.LocalAlbumPageActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.seam.actions.LocalAlbumAction
import com.savvasdalkitsis.uhuruphotos.feature.local.view.implementation.ui.state.LocalAlbumState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LocalAlbumViewModel @Inject constructor(
    localAlbumActionsContext: LocalAlbumActionsContext,
    localAlbumPageActionsContext: LocalAlbumPageActionsContext,
    galleryEffectHandler: GalleryEffectHandler,
    localAlbumEffectHandler: LocalAlbumEffectHandler,
) : ViewModel(), HasActionableState<
        Pair<GalleryState, LocalAlbumState>,
        Either<GalleryAction, LocalAlbumAction>,
> by Seam(
    CompositeActionHandler(
        ActionHandlerWithContext(localAlbumPageActionsContext),
        ActionHandlerWithContext(localAlbumActionsContext),
    ),
    CompositeEffectHandler(
        galleryEffectHandler,
        localAlbumEffectHandler,
    ),
    GalleryState() to LocalAlbumState()
), HasInitializer<LocalAlbumNavigationRoute> {
    override suspend fun initialize(initializerData: LocalAlbumNavigationRoute) {
        val albumId = initializerData.albumId
        action(Either.Right(Load(albumId)))
        action(Either.Left(LoadCollage(GalleryId(albumId, "local:$albumId"))))
    }
}