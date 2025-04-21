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
package com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ClickedOnDetailsEntry
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_aspect_ratio
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_camera
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_camera_roll
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_file_tree
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_fingerprint
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_folder_network
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_image_aspect_ratio
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_iso
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_lens
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_refresh
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_save
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_shutter_speed
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_videocam
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.details
import uhuruphotos_android.foundation.strings.api.generated.resources.nothing_here_yet

@Composable
internal fun LightboxInfoMetadata(
    mediaItem: SingleMediaItemState,
    action: (LightboxAction) -> Unit
) {
    Column(
        verticalArrangement = spacedBy(8.dp)
    ) {
        SectionHeader(
            title = stringResource(string.details)
        ) {
            Box(
                modifier = Modifier
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            ) {
                if (mediaItem.loadingDetails) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.Center)
                    )
                } else {
                    UhuruActionIcon(
                        modifier = Modifier
                            .align(Alignment.Center),
                        iconModifier = Modifier.alpha(0.6f),
                        onClick = { action(Refresh) },
                        icon = drawable.ic_refresh,
                    )
                }
            }
        }
        mediaItem.details.wh?.let { (w, h) -> "$w x $h" }
            .Entry(drawable.ic_image_aspect_ratio, action)
        mediaItem.details.megapixels
            .Entry(drawable.ic_aspect_ratio, action)
        mediaItem.details.size
            .Entry(drawable.ic_save, action)
        mediaItem.details.camera
            .Entry(drawable.ic_camera, action)
        mediaItem.details.fStop
            .Entry(drawable.ic_lens, action)
        mediaItem.details.shutterSpeed
            .Entry(drawable.ic_shutter_speed, action)
        mediaItem.details.focalLength
            .Entry(drawable.ic_videocam, action)
        mediaItem.details.focalLength35Equivalent
            .Entry(drawable.ic_camera_roll, action)
        mediaItem.details.isoSpeed
            .Entry(drawable.ic_iso, action)
        mediaItem.details.hash
            ?.Entry(drawable.ic_fingerprint, action)
        mediaItem.details.remotePaths.forEach {
            it.Entry(drawable.ic_folder_network, action)
        }
        mediaItem.details.localPaths.forEach {
            it.Entry(drawable.ic_file_tree, action)
        }
        if (mediaItem.details.isEmpty) {
            Text(stringResource(string.nothing_here_yet))
        }
    }
}

@Composable
private fun String?.Entry(
    icon: DrawableResource,
    action: (LightboxAction) -> Unit
) {
    this?.let {
        LightboxInfoIconEntry(
            modifier = Modifier.clickable { action(ClickedOnDetailsEntry(it)) },
            icon = icon,
        ) {
            Text(text = it, textAlign = TextAlign.Start)
        }
    }
}