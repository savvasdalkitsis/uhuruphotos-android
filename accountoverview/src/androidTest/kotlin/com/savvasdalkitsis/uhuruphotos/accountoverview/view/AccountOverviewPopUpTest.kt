package com.savvasdalkitsis.uhuruphotos.accountoverview.view

import androidx.compose.material.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.savvasdalkitsis.uhuruphotos.userbadge.api.view.state.UserInformationState
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
                visible = true,
                userInformationState = UserInformationState(),
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