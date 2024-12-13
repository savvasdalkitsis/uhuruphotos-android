/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.checkable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.CustomColors

@Composable
fun Checkable(
    modifier: Modifier = Modifier,
    id: Any,
    shape: Shape = RectangleShape,
    selectedBorder: BorderStroke? = null,
    selectionMode: SelectionMode,
    selectionBackgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    fallbackColor: Color = Color.Transparent,
    editable: Boolean = true,
    selectedScale: Float = 0.85f,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val scale = remember(id) { Animatable(1f) }
    val backgroundColor = remember(selectionMode) {
        when (selectionMode) {
            SelectionMode.SELECTED -> selectionBackgroundColor
            else -> fallbackColor
        }
    }
    val haptics = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .clip(shape)
            .then(if (selectedBorder != null && selectionMode == SelectionMode.SELECTED)
                Modifier.border(selectedBorder, shape)
            else
                Modifier
            )
            .background(backgroundColor)
            .combinedClickable(
                enabled = editable,
                role = Role.Button,
                onClick = { onClick() },
                onLongClick = {
                    haptics.performHapticFeedback(LongPress)
                    onLongClick()
                }
            )
            .recomposeHighlighter()
    ) {
        Box(
            modifier = Modifier
                .scale(scale.value),
            content = content,
        )
        AnimatedVisibility(visible = selectionMode != SelectionMode.UNDEFINED) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopStart)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(
                        if (selectionMode == SelectionMode.SELECTED)
                            Color.White
                        else
                            Color.Transparent
                    ),
                painter = painterResource(
                    id = if (selectionMode == SelectionMode.SELECTED)
                        R.drawable.ic_check_circle
                    else
                        R.drawable.ic_outline_unselected
                ),
                tint = if (selectionMode == SelectionMode.SELECTED)
                    CustomColors.selected
                else
                    Color.White,
                contentDescription = null
            )
        }
    }
    LaunchedEffect(id, selectionMode) {
        if (selectionMode == SelectionMode.SELECTED) {
            scale.animateTo(selectedScale)
        } else {
            scale.animateTo(1f)
        }
    }
}