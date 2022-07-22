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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.icons.R.drawable.*
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.api.ui.view.SectionHeader
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction
import com.savvasdalkitsis.uhuruphotos.implementation.photos.seam.PhotoAction.ClickedOnDetailsEntry
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.SinglePhotoState

@Composable
internal fun PhotoDetailsMetadata(
    photo: SinglePhotoState,
    action: (PhotoAction) -> Unit
) {
    val metadata = photo.metadata
    val path = photo.path
    if (metadata != null || path != null) {
        Column(
            verticalArrangement = spacedBy(8.dp)
        ) {
            SectionHeader(title = stringResource(string.details))
            if (metadata != null) {
                metadata.exifData.wh?.let { (w, h) -> "$w x $h" }
                    .Entry(ic_image_aspect_ratio, action)
                metadata.exifData.megapixels
                    .Entry(ic_aspect_ratio, action)
                metadata.size
                    .Entry(ic_save, action)
                metadata.exifData.camera
                    .Entry(ic_camera, action)
                metadata.exifData.fStop
                    .Entry(ic_lens, action)
                metadata.exifData.shutterSpeed
                    .Entry(ic_shutter_speed, action)
                metadata.exifData.focalLength
                    .Entry(ic_videocam, action)
                metadata.exifData.focalLength35Equivalent
                    .Entry(ic_camera_roll, action)
                metadata.exifData.isoSpeed
                    .Entry(ic_iso, action)
            }
            if (path != null) {
                photo.path
                    .Entry(ic_file_tree, action)
            }
        }
    }
}

@Composable
private fun String?.Entry(
    @DrawableRes icon: Int,
    action: (PhotoAction) -> Unit
) {
    this?.let {
        PhotoDetailsIconEntry(
            modifier = Modifier.clickable { action(ClickedOnDetailsEntry(it)) },
            icon = icon,
        ) {
            Text(text = it, textAlign = TextAlign.Start)
        }
    }
}