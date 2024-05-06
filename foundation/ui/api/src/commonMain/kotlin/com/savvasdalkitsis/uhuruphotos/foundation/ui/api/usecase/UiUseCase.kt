package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.usecase

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.window.UiController

interface UiUseCase {

    var keyboardController: SoftwareKeyboardController
    var systemUiController: UiController
    var haptics: HapticFeedback

    fun hideKeyboard()
    fun setSystemBarsVisibility(visible: Boolean)
    fun performLongPressHaptic()
}