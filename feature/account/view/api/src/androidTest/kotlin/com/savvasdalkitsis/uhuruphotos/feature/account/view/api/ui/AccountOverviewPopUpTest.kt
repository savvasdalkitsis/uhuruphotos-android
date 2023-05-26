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
package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui

import androidx.compose.material.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountOverviewPopUpTest {

    @get:Rule
    val compose = createComposeRule()
    val dismiss = mockk<() -> Unit>(relaxed = true)
    val logOut = mockk<() -> Unit>(relaxed = true)
    val editServer = mockk<() -> Unit>(relaxed = true)
    val settings = mockk<() -> Unit>(relaxed = true)

    @Before
    fun setUp() {
        compose.setContent {
            Text("outside")
            AccountOverviewPopUp(
                state = AvatarState(),
                onDismiss = dismiss,
                onLogoutClicked = logOut,
                onEditServerClicked = editServer,
                onSettingsClicked = settings,
            )
        }
    }

    @Test
    fun dismissesPopUpWhenTouchingOutside() = with(compose) {
        onNodeWithText("outside").performClick()

        verify { dismiss() }
    }

    @Test
    fun editsServerUrl() = with(compose) {
        onNodeWithContentDescription("Edit server url").performClick()

        verify { editServer() }
    }

    @Test
    fun navigatesToSettings() = with(compose) {
        onNodeWithText("Settings").performClick()

        verify { settings() }
    }

    @Test
    fun logsOut() = with(compose) {
        onNodeWithText("Log out").performClick()

        verify { logOut() }
    }

}