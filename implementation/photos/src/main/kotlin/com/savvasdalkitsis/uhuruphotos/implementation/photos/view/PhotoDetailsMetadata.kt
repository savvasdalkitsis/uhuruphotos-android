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
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.icons.R.drawable
import com.savvasdalkitsis.uhuruphotos.api.strings.R.string
import com.savvasdalkitsis.uhuruphotos.api.ui.view.SectionHeader
import com.savvasdalkitsis.uhuruphotos.implementation.photos.usecase.PhotoMetadata
import com.savvasdalkitsis.uhuruphotos.implementation.photos.view.state.SinglePhotoState

@Composable
internal fun PhotoDetailsMetadata(photo: SinglePhotoState) {
    photo.metadata?.let { metadata ->
        Column(
            verticalArrangement = spacedBy(8.dp)
        ) {
            SectionHeader(title = stringResource(string.details))
            IconEntry(drawable.ic_image_aspect_ratio, metadata.exifData.wh?.let { (w, h) -> "$w x $h"})
            IconEntry(drawable.ic_aspect_ratio, metadata.exifData.megapixels)
            IconEntry(drawable.ic_save, metadata.size)
            IconEntry(drawable.ic_camera, metadata.exifData.camera)
            IconEntry(drawable.ic_lens, metadata.exifData.fStop)
            IconEntry(drawable.ic_shutter_speed, metadata.exifData.shutterSpeed)
            IconEntry(drawable.ic_videocam, metadata.exifData.focalLength)
            IconEntry(drawable.ic_camera_roll, metadata.exifData.focalLength35Equivalent)
            IconEntry(drawable.ic_iso, metadata.exifData.isoSpeed)
        }
    }
}


@Composable
private fun IconEntry(@DrawableRes icon: Int, text: String?) {
    text?.let {
        PhotoDetailsIconEntry(icon) {
            Text(text = it, textAlign = TextAlign.Start)
        }
    }
}