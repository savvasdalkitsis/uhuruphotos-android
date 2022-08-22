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

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.lightbox.view.implementation.ui.state.SingleMediaItemState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable

@Composable
internal fun LightboxInfoLocation(mediaItem: SingleMediaItemState) {
    if (mediaItem.location.isNotEmpty()) {
        LightboxInfoIconEntry(
            icon = drawable.ic_location_place,
        ) {
            Text(mediaItem.location)
        }
    }
}