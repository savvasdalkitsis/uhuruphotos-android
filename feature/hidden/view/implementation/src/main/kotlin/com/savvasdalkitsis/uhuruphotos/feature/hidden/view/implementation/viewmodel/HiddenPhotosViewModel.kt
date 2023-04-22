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

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.api.HiddenPhotosNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosAlbumPageActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HiddenPhotosViewModel @Inject constructor(
    hiddenPhotosAlbumPageActionsContext: HiddenPhotosAlbumPageActionsContext,
    hiddenPhotosActionsContext: HiddenPhotosActionsContext,
    effectHandler: HiddenPhotosEffectHandler,
    galleryEffectHandler: GalleryEffectHandler,
) : ViewModel(), HasActionableState<
        Pair<GalleryState, HiddenPhotosState>,
        Either<GalleryAction, HiddenPhotosAction>,
> by Seam(
    CompositeActionHandler(
        ActionHandlerWithContext(hiddenPhotosAlbumPageActionsContext),
        ActionHandlerWithContext(hiddenPhotosActionsContext),
    ),
    CompositeEffectHandler(
        galleryEffectHandler,
        effectHandler,
    ),
    GalleryState(collageState = CollageState()) to HiddenPhotosState()
), HasInitializer<Either<GalleryAction, HiddenPhotosAction>, HiddenPhotosNavigationRoute> {
    override suspend fun initialize(
        initializerData: HiddenPhotosNavigationRoute,
        action: (Either<GalleryAction, HiddenPhotosAction>) -> Unit
    ) {
        action(Either.Left(LoadCollage(GalleryId(0, "hidden"))))
        action(Either.Right(Load))
    }
}