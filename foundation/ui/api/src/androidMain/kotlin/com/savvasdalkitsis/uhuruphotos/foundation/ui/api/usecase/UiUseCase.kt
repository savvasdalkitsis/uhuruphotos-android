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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.google.accompanist.systemuicontroller.SystemUiController

interface UiUseCase {

    var keyboardController: SoftwareKeyboardController
    var systemUiController: SystemUiController
    var haptics: HapticFeedback

    fun hideKeyboard()
    fun setSystemBarsVisibility(visible: Boolean)
    fun performLongPressHaptic()
}