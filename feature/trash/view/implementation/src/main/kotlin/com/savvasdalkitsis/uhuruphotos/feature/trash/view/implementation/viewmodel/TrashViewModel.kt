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
package com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.viewmodel

import androidx.lifecycle.ViewModel
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.GalleryId
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.LoadCollage
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.api.navigation.TrashNavigationRoute
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashAlbumPageActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.TrashEffectHandler
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.seam.actions.TrashAction
import com.savvasdalkitsis.uhuruphotos.feature.trash.view.implementation.state.TrashState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandlerWithContext
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.HasActionableState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeEffectHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.HasInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Seam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private typealias TrashCompositeState = Pair<GalleryState, TrashState>
private typealias TrashCompositeAction = Either<GalleryAction, TrashAction>

@HiltViewModel
internal class TrashViewModel @Inject constructor(
    trashActionsContext: TrashActionsContext,
    trashAlbumPageActionsContext: TrashAlbumPageActionsContext,
    trashEffectHandler: TrashEffectHandler,
    galleryEffectHandler: GalleryEffectHandler,
) : ViewModel(),
    HasActionableState<TrashCompositeState, TrashCompositeAction> by Seam(
        CompositeActionHandler(
            ActionHandlerWithContext(trashAlbumPageActionsContext),
            ActionHandlerWithContext(trashActionsContext),
        ),
        CompositeEffectHandler(
            galleryEffectHandler,
            trashEffectHandler,
        ),
        GalleryState() to TrashState()
    ),
    HasInitializer<TrashCompositeAction, TrashNavigationRoute> {

    override suspend fun initialize(
        initializerData: TrashNavigationRoute,
        action: (TrashCompositeAction) -> Unit,
    ) {
        action(Either.Left(LoadCollage(GalleryId(0, "trash"))))
        action(Either.Right(Load))
    }
}