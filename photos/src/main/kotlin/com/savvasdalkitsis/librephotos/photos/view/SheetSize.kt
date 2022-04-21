package com.savvasdalkitsis.librephotos.photos.view

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

class SheetSize private constructor(
    internal var size: DpSize
) {

    companion object {
        @Composable
        fun rememberSheetSize(): MutableState<SheetSize> {
            val size by remember { mutableStateOf(DpSize(0.dp, 0.dp)) }
            return remember { mutableStateOf(SheetSize(size)) }
        }
    }
}

internal fun Modifier.adjustingSheetSize(sheetSize: SheetSize) = composed {
    val density = LocalDensity.current
    onGloballyPositioned { coordinates ->
        with(density) {
            sheetSize.size = coordinates.size.toSize().toDpSize()
        }
    }
}