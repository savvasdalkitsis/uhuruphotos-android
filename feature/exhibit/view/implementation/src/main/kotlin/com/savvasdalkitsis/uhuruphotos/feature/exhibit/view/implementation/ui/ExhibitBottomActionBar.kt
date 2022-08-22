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
package com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitAction
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitAction.AskForMediaItemRestoration
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitAction.AskForMediaItemTrashing
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitAction.ShareMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.seam.ExhibitAction.UseMediaItemAs
import com.savvasdalkitsis.uhuruphotos.feature.exhibit.view.implementation.ui.state.ExhibitState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIconWithText

@Composable
fun ExhibitBottomActionBar(
    state: ExhibitState,
    index: Int,
    action: (ExhibitAction) -> Unit,
) {
    Row {
        AnimatedVisibility(
            modifier = Modifier
                .weight(1f),
            visible = state.media[index].showShareIcon,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            ActionIconWithText(
                onClick = { action(ShareMediaItem) },
                icon = drawable.ic_share,
                text = stringResource(string.share),
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .weight(1f),
            visible = state.media[index].showUseAsIcon,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            ActionIconWithText(
                onClick = { action(UseMediaItemAs) },
                icon = drawable.ic_open_in_new,
                text = stringResource(string.use_as),
            )
        }
        if (state.showRestoreButton) {
            ActionIconWithText(
                onClick = { action(AskForMediaItemRestoration) },
                modifier = Modifier
                    .weight(1f),
                icon = drawable.ic_restore_from_trash,
                text = stringResource(string.restore),
            )
        }
        if (state.media[index].showDeleteButton) {
            ActionIconWithText(
                onClick = { action(AskForMediaItemTrashing) },
                modifier = Modifier
                    .weight(1f),
                icon = drawable.ic_delete,
                text = stringResource(string.delete),
            )
        }
    }
}