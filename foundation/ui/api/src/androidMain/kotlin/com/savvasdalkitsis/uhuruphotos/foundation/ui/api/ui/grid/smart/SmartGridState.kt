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

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.simple.SmartSimpleGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.staggered.SmartStaggeredGridState

interface SmartGridState {
    suspend fun animateScrollToItem(index: Int, scrollOffset: Int = 0)
    val isScrollInProgress: Boolean
    val layoutInfo: SmartLayoutInfo
    val firstVisibleItemIndex: Int
    val firstVisibleItemScrollOffset: Int
}



@Composable
fun rememberSmartGridState(staggered: Boolean): SmartGridState = when {
    staggered -> SmartStaggeredGridState(rememberLazyStaggeredGridState())
    else -> SmartSimpleGridState(rememberLazyGridState())
}.let {
    remember(staggered) { it }
}