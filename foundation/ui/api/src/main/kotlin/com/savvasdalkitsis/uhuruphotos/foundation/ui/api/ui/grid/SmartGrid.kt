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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.simple.SmartSimpleGridScope
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.simple.SmartSimpleGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.staggered.SmartStaggeredGridScope
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.staggered.SmartStaggeredGridState

@Composable
fun SmartGrid(
    modifier: Modifier = Modifier,
    gridState: SmartGridState,
    columns: Int,
    verticalItemSpacing: Dp = 0.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(0.dp),
    content: SmartGridScope.() -> Unit,
) {
    val staggeredGridScope = remember {
        SmartStaggeredGridScope()
    }
    val simpleGridScope = remember {
        SmartSimpleGridScope()
    }
    when(gridState) {
        is SmartStaggeredGridState -> LazyVerticalStaggeredGrid(
            modifier = modifier,
            columns = remember(columns) {
                StaggeredGridCells.Fixed(columns)
            },
            state = gridState.lazyStaggeredGridState,
            verticalItemSpacing = verticalItemSpacing,
            horizontalArrangement = horizontalArrangement,
        ) {
            staggeredGridScope.lazyStaggeredGridScope.value = this
            content(staggeredGridScope)
        }
        is SmartSimpleGridState -> LazyVerticalGrid(
            modifier = modifier,
            columns = remember(columns) {
                GridCells.Fixed(columns)
            },
            state = gridState.lazyGridState,
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = Arrangement.spacedBy(verticalItemSpacing),
        ) {
            simpleGridScope.lazyGridScope.value = this
            content(simpleGridScope)
        }
    }
}