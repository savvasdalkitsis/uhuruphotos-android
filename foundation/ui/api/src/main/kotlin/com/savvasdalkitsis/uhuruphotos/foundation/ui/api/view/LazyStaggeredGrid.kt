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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.view

import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LazyStaggeredGrid(
    modifier: Modifier = Modifier,
    columnCount: Int,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable LazyStaggeredGridScope.() -> Unit,
) {
    val states: Array<LazyListState> = (0 until columnCount)
        .map { rememberLazyListState() }
        .toTypedArray()
    val scope = rememberCoroutineScope { Dispatchers.Main.immediate }
    val scroll = rememberScrollableState { delta ->
        scope.launch { states.forEach {  it.scrollBy(-delta) }}
        when {
            delta > 0 && states.all {
                it.firstVisibleItemIndex == 0 && it.firstVisibleItemScrollOffset == 0
            } -> 0f
            else -> delta
        }
    }
    val gridScope = LazyStaggeredGridScope(columnCount)
    content(gridScope)

    Box(modifier = modifier
        .scrollable(scroll, Vertical, flingBehavior = ScrollableDefaults.flingBehavior())
    ) {
        Row {
            for (index in 0 until columnCount) {
                LazyColumn(
                    userScrollEnabled = false,
                    contentPadding = contentPadding,
                    state = states[index],
                    modifier = Modifier.weight(1f)
                ) {
                    for ((key, itemContent) in gridScope.items[index]) {
                        item(key = key) {
                            itemContent()
                        }
                    }
                }
            }
        }
    }
}

class LazyStaggeredGridScope(
    private val columnCount: Int,
) {

    private var currentIndex = 0
    val items: Array<MutableList<Pair<Any?, @Composable () -> Unit>>> = Array(columnCount) { mutableListOf() }

    fun item(key: Any? = null, content: @Composable () -> Unit) {
        items[currentIndex % columnCount] += key to content
        currentIndex += 1
    }
}