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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api

import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.SystemUiController

object NoOpSystemUiController : SystemUiController {

    override var isNavigationBarContrastEnforced = false
    override var isNavigationBarVisible = true
    override var isStatusBarVisible = true
    override var navigationBarDarkContentEnabled = false
    override var statusBarDarkContentEnabled = false
    override var systemBarsBehavior: Int = 0

    override fun setNavigationBarColor(
        color: Color,
        darkIcons: Boolean,
        navigationBarContrastEnforced: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) {
    }

    override fun setStatusBarColor(
        color: Color,
        darkIcons: Boolean,
        transformColorForLightContent: (Color) -> Color
    ) {
    }
}
