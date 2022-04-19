package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.SystemUiController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ControllersProvider @Inject constructor(
    val intentLauncher: IntentLauncher,
) {

    var keyboardController: SoftwareKeyboardController? = null
    var navController: NavHostController? = null
    var focusRequester: FocusRequester? = null
    var systemUiController: SystemUiController? = null
    var haptics: HapticFeedback? = null
}
