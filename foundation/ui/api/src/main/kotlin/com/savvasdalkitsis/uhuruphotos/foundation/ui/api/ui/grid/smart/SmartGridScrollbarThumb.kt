/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.simple.InternalLazyGridScrollbar
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.simple.SmartSimpleGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.staggered.InternalLazyStaggeredGridScrollbar
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.staggered.SmartStaggeredGridState
import my.nanihadesuka.compose.ScrollbarSelectionMode

@Composable
fun SmartGridScrollbarThumb(
    contentPadding: PaddingValues,
    gridState: SmartGridState,
    showScrollbarHint: Boolean,
    scrollText: String
) {
    Box(
        modifier = Modifier
            .recomposeHighlighter()
            .padding(contentPadding)
    ) {
        InternalScrollbarThumb(
            gridState = gridState,
        ) { _, isThumbSelected ->
            val show = showScrollbarHint && isThumbSelected
            AnimatedVisibility(
                modifier = Modifier.padding(end = 8.dp),
                enter = fadeIn() + scaleIn(transformOrigin = TransformOrigin(1f, 0.5f)),
                exit = fadeOut() + scaleOut(transformOrigin = TransformOrigin(1f, 0.5f)),
                visible = show,
            ) {
                Box(modifier = Modifier.padding(end = 52.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colors.onBackground.copy(alpha = 0.8f))
                            .padding(8.dp)
                            .animateContentSize(),
                    ) {
                        val haptic = LocalHapticFeedback.current
                        Text(
                            text = scrollText,
                            color = MaterialTheme.colors.onPrimary,
                        )
                        LaunchedEffect(scrollText) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InternalScrollbarThumb(
    gridState: SmartGridState,
    indicatorContent: (@Composable (index: Int, isThumbSelected: Boolean) -> Unit)? = null,
) {
    val primary = MaterialTheme.colors.primary
    val thumbColor = remember {
        primary.copy(alpha = 0.7f)
    }
    when (gridState) {
        is SmartStaggeredGridState ->
            InternalLazyStaggeredGridScrollbar(
                gridState = gridState.lazyStaggeredGridState,
                thickness = 8.dp,
                selectionMode = ScrollbarSelectionMode.Thumb,
                thumbColor = thumbColor,
                thumbSelectedColor = primary,
                indicatorContent = indicatorContent,
            )
        is SmartSimpleGridState ->
            InternalLazyGridScrollbar(
                gridState = gridState.lazyGridState,
                thickness = 8.dp,
                selectionMode = ScrollbarSelectionMode.Thumb,
                thumbColor = thumbColor,
                thumbSelectedColor = primary,
                indicatorContent = indicatorContent,
            )
    }
}