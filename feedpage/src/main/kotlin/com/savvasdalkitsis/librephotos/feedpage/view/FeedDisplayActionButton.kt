package com.savvasdalkitsis.librephotos.feedpage.view

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.librephotos.feed.view.state.FeedDisplay
import com.savvasdalkitsis.librephotos.ui.view.ActionBarIcon

@Composable
fun FeedDisplayActionButton(
    onShow: () -> Unit,
    onHide: () -> Unit,
    onChange: (FeedDisplay) -> Unit,
    expanded: Boolean,
    currentFeedDisplay: FeedDisplay,
) {

    Box {
        ActionBarIcon(
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
            onClick = onShow,
            icon = currentFeedDisplay.iconResource,
            contentDescription = "feed display",
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onHide,
        ) {
            FeedDisplay.values().reversedArray().forEach { display ->
                FeedDisplayDropDownItem(onChange, display, currentFeedDisplay)
            }
        }
    }
}

@Composable
private fun FeedDisplayDropDownItem(
    onChange: (FeedDisplay) -> Unit,
    display: FeedDisplay,
    currentFeedDisplay: FeedDisplay
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