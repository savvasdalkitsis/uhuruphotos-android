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

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemInfo
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartLayoutItemInfo

class SmartStaggeredLayoutItemInfo(
    private val itemInfo: LazyStaggeredGridItemInfo,
) : SmartLayoutItemInfo {
    override val key: Any
        get() = itemInfo.key
    override val offset: IntOffset
        get() = itemInfo.offset
    override val size: IntSize
        get() = itemInfo.size

}
