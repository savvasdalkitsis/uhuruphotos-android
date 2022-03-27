package com.savvasdalkitsis.librephotos.navigation

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.navigation.NavHostController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ControllersProvider @Inject constructor() {

    @ExperimentalComposeUiApi
    var keyboardController: SoftwareKeyboardController? = null
    var navController: NavHostController? = null
    var focusRequester: FocusRequester? = null

}
