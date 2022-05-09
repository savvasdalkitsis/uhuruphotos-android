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
package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.uhuruphotos.feed.view.state.FeedDisplays
import com.savvasdalkitsis.uhuruphotos.ui.view.ActionIcon

@Composable
fun FeedDisplayActionButton(
    onChange: (FeedDisplay) -> Unit,
    currentFeedDisplay: FeedDisplay,
) {
    var isOpen by remember { mutableStateOf(false) }
    Box {
        ActionIcon(
            modifier = Modifier
                .pointerInput(currentFeedDisplay) {
                    detectVerticalDragGestures { _, dragAmount ->
                        onChange(
                            when {
                                dragAmount > 0 -> currentFeedDisplay.zoomIn
                                else -> currentFeedDisplay.zoomOut
                            }
                        )
                    }
                },
            onClick = { isOpen = true },
            icon = currentFeedDisplay.iconResource,
            contentDescription = "feed display",
        )
        DropdownMenu(
            expanded = isOpen,
            onDismissRequest = { isOpen = false },
        ) {
            FeedDisplays.values().reversedArray().forEach { display ->
                FeedDisplayDropDownItem(display, currentFeedDisplay) {
                    isOpen = false
                    onChange(it)
                }
            }
        }
    }
}

@Composable
private fun FeedDisplayDropDownItem(
    display: FeedDisplays,
    currentFeedDisplay: FeedDisplay,
    onChange: (FeedDisplays) -> Unit
) {
    DropdownMenuItem(
        onClick = { onChange(display) }
    ) {
        Row {
            RadioButton(
                selected = currentFeedDisplay == display,
                onClick = { onChange(display) }
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                text = display.friendlyName
            )
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp),
                painter = painterResource(id = display.iconResource),
                contentDescription = display.friendlyName
            )
        }
    }
}