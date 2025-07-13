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
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
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
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIconWithText
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_arrow_up
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_crop
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_delete
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_edit
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_open_in_new
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_restore_from_trash
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_share
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.crop
import uhuruphotos_android.foundation.strings.api.generated.resources.delete
import uhuruphotos_android.foundation.strings.api.generated.resources.dismiss
import uhuruphotos_android.foundation.strings.api.generated.resources.edit
import uhuruphotos_android.foundation.strings.api.generated.resources.restore
import uhuruphotos_android.foundation.strings.api.generated.resources.share
import uhuruphotos_android.foundation.strings.api.generated.resources.use_as

@Composable
fun LightboxBottomActionBar(
    mediaItem: SingleMediaItemState,
    showRestoreButton: Boolean,
    action: (LightboxAction) -> Unit,
) {
    val apps = mediaItem.showEditApps
    when {
        apps.isEmpty() -> LightboxBottomActionBarOptions(mediaItem, showRestoreButton, action)
        else -> LightboxBottomActionBarEdit(mediaItem, action)
    }
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
            UhuruActionIcon(
                onClick = { action(HideEditOptions) },
                icon = drawable.ic_arrow_up,
                iconModifier = Modifier.rotate(180f),
                contentDescription = stringResource(string.dismiss),
            )
        }
        item("crop") {
            UhuruActionIconWithText(
                onClick = { action(CropMediaItem) },
                icon = drawable.ic_crop,
                text = stringResource(string.crop),
            )
        }
        mediaItem.showEditApps.forEach { app ->
            item(app.iconResource) {
                val pm = LocalContext.current.packageManager
                UhuruActionIconWithText(
                    onClick = { action(EditMediaItemExternally(app)) },
                    painter = rememberDrawablePainter(app.loadIcon(pm)),
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
                UhuruActionIconWithText(
                    modifier = Modifier.animateItem(),
                    onClick = { action(ShareMediaItem) },
                    icon = drawable.ic_share,
                    text = stringResource(string.share),
                )
            }
        }
        if (mediaItem.showUseAsIcon) {
            item("useAs") {
                UhuruActionIconWithText(
                    modifier = Modifier.animateItem(),
                    onClick = { action(UseMediaItemAs) },
                    icon = drawable.ic_open_in_new,
                    text = stringResource(string.use_as),
                )
            }
        }
        if (mediaItem.showEditIcon) {
            item("edit") {
                UhuruActionIconWithText(
                    modifier = Modifier.animateItem(),
                    onClick = { action(EditMediaItem) },
                    icon = drawable.ic_edit,
                    text = stringResource(string.edit),
                )
            }
        }
        if (showRestoreButton) {
            item("restore") {
                UhuruActionIconWithText(
                    modifier = Modifier.animateItem(),
                    onClick = { action(AskForMediaItemRestoration) },
                    icon = drawable.ic_restore_from_trash,
                    text = stringResource(string.restore),
                )
            }
        }
        if (mediaItem.showDeleteButton) {
            item("delete") {
                UhuruActionIconWithText(
                    modifier = Modifier.animateItem(),
                    onClick = { action(AskForMediaItemTrashing) },
                    icon = drawable.ic_delete,
                    text = stringResource(string.delete),
                )
            }
        }
    }
}