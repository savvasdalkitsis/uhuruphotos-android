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
package com.savvasdalkitsis.uhuruphotos.account.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.pressBack
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LogOutConfirmationDialogTest {

    @get:Rule
    val compose = createComposeRule()
    val dismiss = mockk<() -> Unit>(relaxed = true)
    val logOut = mockk<() -> Unit>(relaxed = true)

    @Before
    fun setUp() {
        compose.setContent {
            LogOutConfirmationDialog(
                onDismiss = dismiss,
                onLogOut = logOut,
            )
        }
    }

    @Test
    fun dismissesDialogWhenSelectingNo() = with(compose) {
        onNodeWithText("No").performClick()

        verify { dismiss() }
    }


    @Test
    fun dismissesDialogWhenPressingBack() = with(compose) {
        pressBack()

        verify { dismiss() }
    }

    @Test
    fun logsOutSelectingYes() = with(compose) {
        onNodeWithText("Yes").performClick()

        verify { logOut() }
    }
}