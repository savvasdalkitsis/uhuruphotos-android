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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.implementation.usecase

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.savvasdalkitsis.uhuruphotos.foundation.launchers.api.onMain
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase.UiUseCase
import se.ansman.dagger.auto.AutoBind
import javax.inject.Inject
import javax.inject.Singleton

@AutoBind
@Singleton
class UiUseCase @Inject constructor(
) : UiUseCase {

    lateinit var keyboardController: SoftwareKeyboardController
    lateinit var systemUiController: SystemUiController
    lateinit var haptics: HapticFeedback

    override fun hideKeyboard() {
        onMain {
            keyboardController.hide()
        }
    }

    override fun setSystemBarsVisibility(visible: Boolean) {
        onMain {
            systemUiController.isSystemBarsVisible = visible
        }
    }

    override fun performLongPressHaptic() {
        onMain {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

}