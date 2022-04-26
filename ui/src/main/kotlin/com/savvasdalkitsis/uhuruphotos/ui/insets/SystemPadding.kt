package com.savvasdalkitsis.uhuruphotos.ui.insets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.End
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Start
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun systemPadding(sides: WindowInsetsSides) = WindowInsets
    .displayCutout.union(WindowInsets.systemBars)
    .only(sides)
    .asPaddingValues()

@Composable
fun insetsStart() = systemPadding(Start).calculateStartPadding(LocalLayoutDirection.current)
@Composable
fun insetsEnd() = systemPadding(End).calculateEndPadding(LocalLayoutDirection.current)
@Composable
fun insetsTop() = systemPadding(Top).calculateTopPadding()
@Composable
fun insetsBottom() = systemPadding(Bottom).calculateBottomPadding()