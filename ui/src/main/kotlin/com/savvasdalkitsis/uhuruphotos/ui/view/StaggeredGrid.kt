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
package com.savvasdalkitsis.uhuruphotos.ui.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    columnCount: Int,
    syncScrolling: Boolean = true,
    content: @Composable StaggeredGridScope.() -> Unit,
) {
    val states: Array<ScrollState> = (0 until columnCount)
        .map { rememberScrollState() }
        .toTypedArray()
    val scope = rememberCoroutineScope { Dispatchers.Main.immediate }
    val scroll = rememberScrollableState { delta ->
        if (syncScrolling) {
            scope.launch { states.forEach { it.scrollBy(-delta) } }
        }
        when {
            delta > 0 && states.all {
                it.value == 0
            } -> 0f
            else -> delta
        }
    }
    val gridScope = StaggeredGridScope(columnCount)
    content(gridScope)

    Box(
        modifier = modifier
            .scrollable(
                scroll,
                Orientation.Vertical,
                flingBehavior = ScrollableDefaults.flingBehavior()
            )
    ) {
        Row {
            for (index in 0 until columnCount) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(states[index])
                ) {
                    Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding()))
                    gridScope.items[index].forEach { it() }
                    Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding()))
                }
            }
        }
    }
}

class StaggeredGridScope(
    private val columnCount: Int,
) {

    private var currentIndex = 0
    val items: Array<MutableList<@Composable () -> Unit>> = Array(columnCount) { mutableListOf() }

    fun item(content: @Composable () -> Unit) {
        items[currentIndex % columnCount] += content
        currentIndex += 1
    }
}