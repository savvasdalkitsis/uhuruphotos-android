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
package com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui

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
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplay
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplay
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon

@Composable
fun CollageDisplayActionButton(
    onChange: (CollageDisplay) -> Unit,
    currentCollageDisplay: CollageDisplay,
) {
    var isOpen by remember { mutableStateOf(false) }
    Box {
        ActionIcon(
            modifier = Modifier
                .pointerInput(currentCollageDisplay) {
                    detectVerticalDragGestures { _, dragAmount ->
                        onChange(
                            when {
                                dragAmount > 0 -> currentCollageDisplay.zoomIn
                                else -> currentCollageDisplay.zoomOut
                            }
                        )
                    }
                },
            onClick = { isOpen = true },
            icon = currentCollageDisplay.iconResource,
            contentDescription = stringResource(string.gallery_size),
        )
        DropdownMenu(
            expanded = isOpen,
            onDismissRequest = { isOpen = false },
        ) {
            PredefinedCollageDisplay.entries.toTypedArray().reversedArray().forEach { display ->
                CollageDisplayDropDownItem(display, currentCollageDisplay) {
                    isOpen = false
                    onChange(it)
                }
            }
        }
    }
}

@Composable
private fun CollageDisplayDropDownItem(
    display: PredefinedCollageDisplay,
    currentCollageDisplay: CollageDisplay,
    onChange: (PredefinedCollageDisplay) -> Unit
) {
    DropdownMenuItem(
        onClick = { onChange(display) }
    ) {
        Row {
            RadioButton(
                selected = currentCollageDisplay == display,
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