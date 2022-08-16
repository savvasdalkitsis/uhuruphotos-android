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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

class SheetSize private constructor(
    var size: DpSize
) {

    companion object {
        @Composable
        fun rememberSheetSize(): MutableState<SheetSize> {
            val size by remember { mutableStateOf(DpSize(0.dp, 0.dp)) }
            return remember { mutableStateOf(SheetSize(size)) }
        }
    }
}

fun Modifier.adjustingSheetSize(sheetSize: SheetSize) = composed {
    val density = LocalDensity.current
    onGloballyPositioned { coordinates ->
        with(density) {
            sheetSize.size = coordinates.size.toSize().toDpSize()
        }
    }
}