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
package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam

import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaAction
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.seam.GalleriaEffect
import com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state.GalleriaState
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.ActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.CompositeActionHandler
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Mutation
import javax.inject.Inject

internal class HiddenPhotosCompositeActionHandler @Inject constructor(
    hiddenPhotosAlbumPageActionHandler: HiddenPhotosAlbumPageActionHandler,
    hiddenPhotosActionHandler: HiddenPhotosActionHandler,
) : ActionHandler<
        Pair<GalleriaState, HiddenPhotosState>,
        Either<GalleriaEffect, HiddenPhotosEffect>,
        Either<GalleriaAction, HiddenPhotosAction>,
        Mutation<Pair<GalleriaState, HiddenPhotosState>>
        > by CompositeActionHandler(
    handler1 = hiddenPhotosAlbumPageActionHandler,
    handler2 = hiddenPhotosActionHandler
)