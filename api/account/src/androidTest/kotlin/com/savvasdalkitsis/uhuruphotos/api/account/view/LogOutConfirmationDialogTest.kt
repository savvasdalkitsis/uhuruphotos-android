package com.savvasdalkitsis.uhuruphotos.api.account.view

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
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
        Espresso.pressBack()

        verify { dismiss() }
    }

    @Test
    fun logsOutSelectingYes() = with(compose) {
        onNodeWithText("Yes").performClick()

        verify { logOut() }
    }
}