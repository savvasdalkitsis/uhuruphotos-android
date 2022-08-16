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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.GalleryDisplay
import com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state.PredefinedGalleryDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
fun GalleryDisplayActionButton(
    onChange: (GalleryDisplay) -> Unit,
    currentGalleryDisplay: GalleryDisplay,
) {
    var isOpen by remember { mutableStateOf(false) }
    Box {
        ActionIcon(
            modifier = Modifier
                .pointerInput(currentGalleryDisplay) {
                    detectVerticalDragGestures { _, dragAmount ->
                        onChange(
                            when {
                                dragAmount > 0 -> currentGalleryDisplay.zoomIn
                                else -> currentGalleryDisplay.zoomOut
                            }
                        )
                    }
                },
            onClick = { isOpen = true },
            icon = currentGalleryDisplay.iconResource,
            contentDescription = stringResource(string.gallery_size),
        )
        DropdownMenu(
            expanded = isOpen,
            onDismissRequest = { isOpen = false },
        ) {
            PredefinedGalleryDisplay.values().reversedArray().forEach { display ->
                GalleryDisplayDropDownItem(display, currentGalleryDisplay) {
                    isOpen = false
                    onChange(it)
                }
            }
        }
    }
}

@Composable
private fun GalleryDisplayDropDownItem(
    display: PredefinedGalleryDisplay,
    currentGalleryDisplay: GalleryDisplay,
    onChange: (PredefinedGalleryDisplay) -> Unit
) {
    DropdownMenuItem(
        onClick = { onChange(display) }
    ) {
        Row {
            RadioButton(
                selected = currentGalleryDisplay == display,
                onClick = { onChange(display) }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                text = stringResource(display.friendlyName)
            )
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp),
                painter = painterResource(id = display.iconResource),
                contentDescription = null
            )
        }
    }
}