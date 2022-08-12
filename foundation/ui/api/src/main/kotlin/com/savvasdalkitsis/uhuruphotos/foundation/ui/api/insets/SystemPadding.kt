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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.insets

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.End
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Start
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Top
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
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