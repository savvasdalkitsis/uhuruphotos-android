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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.resources.compose.stringResource
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
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIconWithText
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.android.ActionIconWithText
import dev.icerock.moko.resources.ImageResource

@Composable
fun LightboxBottomActionBar(
    mediaItem: SingleMediaItemState,
    showRestoreButton: Boolean,
    action: (LightboxAction) -> Unit,
) {
    val apps = mediaItem.showEditApps
//    AnimatedContent(
//        targetState = mediaItem.showEditApps,
//        transitionSpec = {
//            fadeIn() + slideInVertically { it } togetherWith fadeOut() + slideOutVertically { it }
//        },
//        label = "editApps",
//    ) { apps ->
        when {
            apps.isEmpty() -> LightboxBottomActionBarOptions(mediaItem, showRestoreButton, action)
            else -> LightboxBottomActionBarEdit(mediaItem, action)
        }
//    }
}

@Composable
fun LightboxBottomActionBarEdit(
    mediaItem: SingleMediaItemState,
    action: (LightboxAction) -> Unit,
) {
    BackHandler {
        action(HideEditOptions)
    }
    LazyRow(
        modifier = Modifier
            .recomposeHighlighter()
            .fillMaxWidth(),
        horizontalArrangement = spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item("start") {
            Spacer(Modifier.width(8.dp))
        }
        item("dismiss") {
            ActionIcon(
                onClick = { action(HideEditOptions) },
                icon = images.ic_arrow_up,
                iconModifier = Modifier.rotate(180f),
                contentDescription = stringResource(strings.dismiss),
            )
        }
        item("crop") {
            ActionIconWithText(
                onClick = { action(CropMediaItem) },
                icon = images.ic_crop,
                text = stringResource(strings.crop),
            )
        }
        mediaItem.showEditApps.forEach { app ->
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
    mediaItem: SingleMediaItemState,
    showRestoreButton: Boolean,
    action: (LightboxAction) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .recomposeHighlighter()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        if (mediaItem.showShareIcon) {
            item("share") {
                ActionIconWithText(
                    modifier = Modifier.animateItemPlacement(),
                    onClick = { action(ShareMediaItem) },
                    icon = images.ic_share,
                    text = stringResource(strings.share),
                )
            }
        }
        if (mediaItem.showUseAsIcon) {
            item("useAs") {
                ActionIconWithText(
                    modifier = Modifier.animateItemPlacement(),
                    onClick = { action(UseMediaItemAs) },
                    icon = images.ic_open_in_new,
                    text = stringResource(strings.use_as),
                )
            }
        }
        if (mediaItem.showEditIcon) {
            item("edit") {
                ActionIconWithText(
                    modifier = Modifier.animateItemPlacement(),
                    onClick = { action(EditMediaItem) },
                    icon = images.ic_edit,
                    text = stringResource(strings.edit),
                )
            }
        }
        if (showRestoreButton) {
            item("restore") {
                ActionIconWithText(
                    modifier = Modifier.animateItemPlacement(),
                    onClick = { action(AskForMediaItemRestoration) },
                    icon = images.ic_restore_from_trash,
                    text = stringResource(strings.restore),
                )
            }
        }
        if (mediaItem.showDeleteButton) {
            item("delete") {
                ActionIconWithText(
                    modifier = Modifier.animateItemPlacement(),
                    onClick = { action(AskForMediaItemTrashing) },
                    icon = images.ic_delete,
                    text = stringResource(strings.delete),
                )
            }
        }
    }
}