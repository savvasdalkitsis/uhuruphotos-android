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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.AskForMediaItemRestoration
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.AskForMediaItemTrashing
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.CropMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.EditMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.EditMediaItemExternally
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.HideEditOptions
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ShareMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.UseMediaItemAs
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.LightboxState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIconWithText

@Composable
fun LightboxBottomActionBar(
    state: LightboxState,
    index: Int,
    action: (LightboxAction) -> Unit,
) {
    AnimatedContent(
        targetState = state.showEditApps,
        transitionSpec = {
            fadeIn() + slideInVertically { it } togetherWith fadeOut() + slideOutVertically { it }
        },
        label = "editApps",
    ) { apps ->
        when {
            apps.isEmpty() -> LightboxBottomActionBarOptions(state, index, action)
            else -> LightboxBottomActionBarEdit(state, action)
        }
    }
}

@Composable
fun LightboxBottomActionBarEdit(
    state: LightboxState,
    action: (LightboxAction) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item("start") {
            Spacer(Modifier.width(8.dp))
        }
        item("dismiss") {
            ActionIcon(
                onClick = { action(HideEditOptions) },
                icon = drawable.ic_arrow_up,
                iconModifier = Modifier.rotate(180f),
                contentDescription = stringResource(string.dismiss),
            )
        }
        item("crop") {
            ActionIconWithText(
                onClick = { action(CropMediaItem) },
                icon = drawable.ic_crop,
                text = stringResource(string.crop),
            )
        }
        state.showEditApps.forEach { app ->
            item(app.iconResource) {
                val pm = LocalContext.current.packageManager
                ActionIconWithText(
                    onClick = { action(EditMediaItemExternally(app)) },
                    icon = app.loadIcon(pm),
                    text = app.loadLabel(pm).toString(),
                )
            }
        }
        item("end") {
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
fun LightboxBottomActionBarOptions(
    state: LightboxState,
    index: Int,
    action: (LightboxAction) -> Unit,
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
        AnimatedVisibility(
            modifier = Modifier
                .weight(1f),
            visible = state.media[index].showEditIcon,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            ActionIconWithText(
                onClick = { action(EditMediaItem) },
                icon = drawable.ic_edit,
                text = stringResource(string.edit),
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