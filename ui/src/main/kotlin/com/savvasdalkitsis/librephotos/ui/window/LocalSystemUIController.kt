package com.savvasdalkitsis.librephotos.ui.window

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.google.accompanist.systemuicontroller.SystemUiController

val LocalSystemUiController: ProvidableCompositionLocal<SystemUiController> =
    compositionLocalOf { throw IllegalStateException("Not initialised") }