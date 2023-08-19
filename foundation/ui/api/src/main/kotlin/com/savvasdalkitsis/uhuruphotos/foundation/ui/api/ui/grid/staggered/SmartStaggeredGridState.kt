package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.staggered

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.SmartGridState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.SmartLayoutInfo

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
}