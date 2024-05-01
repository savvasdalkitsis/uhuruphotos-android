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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.simple

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridItemScope
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.grid.smart.SmartGridScope

class SmartSimpleGridScope : SmartGridScope {
    val lazyGridScope: MutableState<LazyGridScope?> = mutableStateOf(null)
    private val itemScope = SmartSimpleGridItemScope()
    override fun item(
        key: Any?,
        contentType: Any?,
        fullLine: Boolean,
        content: @Composable (SmartGridItemScope.() -> Unit)
    ) {
        lazyGridScope.value?.item(key, if (fullLine) {{ GridItemSpan(maxLineSpan) }} else null, contentType) {
            itemScope.lazyGridItemScope.value = this
            content(itemScope)
        }
    }

}