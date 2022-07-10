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
package com.savvasdalkitsis.uhuruphotos.implementation.photos.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.ui.view.ActionIconWithText
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.AskForPhotoTrashing
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.SharePhoto
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.PhotoState
import com.savvasdalkitsis.uhuruphotos.api.icons.R as Icons
import com.savvasdalkitsis.uhuruphotos.api.strings.R as Strings

@Composable
fun PhotoDetailsBottomActionBar(
    state: PhotoState,
    index: Int,
    action: (PhotoAction) -> Unit,
) {
    Row {
        AnimatedVisibility(
            modifier = Modifier
                .weight(1f),
            visible = state.photos[index].showShareIcon,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            ActionIconWithText(
                onClick = { action(SharePhoto) },
                icon = Icons.drawable.ic_share,
                text = stringResource(Strings.string.share),
            )
        }
        ActionIconWithText(
            onClick = { action(AskForPhotoTrashing) },
            modifier = Modifier
                .weight(1f),
            icon = Icons.drawable.ic_delete,
            text = stringResource(Strings.string.delete),
        )
    }
}