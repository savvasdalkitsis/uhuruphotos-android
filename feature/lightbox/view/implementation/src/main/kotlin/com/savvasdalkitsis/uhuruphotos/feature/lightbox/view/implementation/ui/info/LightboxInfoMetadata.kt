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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.ClickedOnDetailsEntry
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.LightboxAction
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.seam.actions.Refresh
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_aspect_ratio
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_camera
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_camera_roll
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_file_tree
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_fingerprint
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_folder_network
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_image_aspect_ratio
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_iso
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_lens
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_refresh
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_save
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_shutter_speed
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable.ic_videocam
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader

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
                    ActionIcon(
                        modifier = Modifier
                            .align(Alignment.Center),
                        iconModifier = Modifier.alpha(0.6f),
                        onClick = { action(Refresh) },
                        icon = ic_refresh,
                    )
                }
            }
        }
        mediaItem.details.wh?.let { (w, h) -> "$w x $h" }
            .Entry(ic_image_aspect_ratio, action)
        mediaItem.details.megapixels
            .Entry(ic_aspect_ratio, action)
        mediaItem.details.size
            .Entry(ic_save, action)
        mediaItem.details.camera
            .Entry(ic_camera, action)
        mediaItem.details.fStop
            .Entry(ic_lens, action)
        mediaItem.details.shutterSpeed
            .Entry(ic_shutter_speed, action)
        mediaItem.details.focalLength
            .Entry(ic_videocam, action)
        mediaItem.details.focalLength35Equivalent
            .Entry(ic_camera_roll, action)
        mediaItem.details.isoSpeed
            .Entry(ic_iso, action)
        mediaItem.details.hash?.let {
            it.Entry(ic_fingerprint, action)
        }
        mediaItem.details.remotePaths.forEach {
            it.Entry(ic_folder_network, action)
        }
        mediaItem.details.localPaths.forEach {
            it.Entry(ic_file_tree, action)
        }
        if (mediaItem.details.isEmpty) {
            Text(stringResource(string.nothing_here_yet))
        }
    }
}

@Composable
private fun String?.Entry(
    @DrawableRes icon: Int,
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