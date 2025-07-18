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
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.seam.action.GalleryAction
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.Gallery
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.HiddenPhotosState
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions.FingerPrintActionPressed
import com.savvasdalkitsis.uhuruphotos.feature.hidden.view.implementation.seam.actions.HiddenPhotosAction
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Left
import com.savvasdalkitsis.uhuruphotos.foundation.seam.api.Either.Right
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.SharedElementId
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.sharedElement
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_fingerprint

@Composable
fun SharedTransitionScope.HiddenPhotosAlbumPage(
    state: Pair<GalleryState, HiddenPhotosState>,
    action: (Either<GalleryAction, HiddenPhotosAction>) -> Unit
) {
    Box(Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background))
    {
        Gallery(
            modifier = Modifier
                .sharedElement(SharedElementId.hidden()),
            titleSharedElementId = SharedElementId.hiddenTitle(),
            state = state.first,
            additionalActionBarContent = {
                if (state.second.displayFingerPrintAction) {
                    UhuruActionIcon(
                        onClick = {
                            action(Right(FingerPrintActionPressed))
                        },
                        icon = drawable.ic_fingerprint,
                    )
                }
            },
            action = {
                action(Left(it))
            }
        )
    }
}
