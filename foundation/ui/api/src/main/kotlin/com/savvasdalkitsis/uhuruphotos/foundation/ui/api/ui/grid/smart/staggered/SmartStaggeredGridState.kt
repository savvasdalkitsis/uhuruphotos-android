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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.staggered

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartLayoutInfo

class SmartStaggeredGridState(
    val lazyStaggeredGridState: LazyStaggeredGridState,
) : SmartGridState {
    override suspend fun animateScrollToItem(index: Int, scrollOffset: Int) {
        lazyStaggeredGridState.animateScrollToItem(index, scrollOffset)
    }

    override val isScrollInProgress: Boolean
        get() = lazyStaggeredGridState.isScrollInProgress
    override val layoutInfo: SmartLayoutInfo
        get() = SmartStaggeredLayoutInfo(lazyStaggeredGridState.layoutInfo)
    override val firstVisibleItemIndex: Int
        get() = lazyStaggeredGridState.firstVisibleItemIndex
    override val firstVisibleItemScrollOffset: Int
        get() = lazyStaggeredGridState.firstVisibleItemScrollOffset
}
