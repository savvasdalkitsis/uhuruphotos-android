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