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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageDisplayState
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.PredefinedCollageDisplayState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon

@Composable
fun CollageDisplayActionButton(
    onChange: (CollageDisplayState) -> Unit,
    currentCollageDisplayState: CollageDisplayState,
) {
    var isOpen by remember { mutableStateOf(false) }
    Box {
        UhuruActionIcon(
            modifier = Modifier
                .pointerInput(currentCollageDisplayState) {
                    detectVerticalDragGestures { _, dragAmount ->
                        onChange(
                            when {
                                dragAmount > 0 -> currentCollageDisplayState.zoomIn
                                else -> currentCollageDisplayState.zoomOut
                            }
                        )
                    }
                },
            onClick = { isOpen = true },
            icon = currentCollageDisplayState.iconResource,
            contentDescription = stringResource(string.gallery_size),
        )
        DropdownMenu(
            expanded = isOpen,
            onDismissRequest = { isOpen = false },
        ) {
            PredefinedCollageDisplayState.entries.toTypedArray().reversedArray().forEach { display ->
                CollageDisplayDropDownItem(display, currentCollageDisplayState) {
                    isOpen = false
                    onChange(it)
                }
            }
        }
    }
}

@Composable
private fun CollageDisplayDropDownItem(
    display: PredefinedCollageDisplayState,
    currentCollageDisplayState: CollageDisplayState,
    onChange: (PredefinedCollageDisplayState) -> Unit
) {
    DropdownMenuItem(
        text = {
            Row {
                RadioButton(
                    selected = currentCollageDisplayState == display,
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
        },
        onClick = { onChange(display) }
    )
}