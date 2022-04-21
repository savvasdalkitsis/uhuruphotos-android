package com.savvasdalkitsis.librephotos.ui.layout

import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl

val LayoutDirection.reverse get() = when(this) {
    Ltr -> Rtl
    Rtl -> Ltr
}