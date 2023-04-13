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
package com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.AuthenticationUseCase
import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.usecase.ServerUseCase
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.AttemptChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ChangeServerUrlTo
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.UrlTyped
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.HideUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.ShowUnsecureServerConfirmation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.ServerMutation.ShowUrlValidation
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.seam.actions.ServerAction
import com.savvasdalkitsis.uhuruphotos.feature.server.view.implementation.ui.ServerState.ServerUrl
import com.savvasdalkitsis.uhuruphotos.feature.settings.domain.api.usecase.SettingsUseCase
import io.mockk.Called
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ServerActionsContextTest {

    private val serverUseCase = mockk<ServerUseCase>(relaxed = true)
    private val authenticationUseCase = mockk<AuthenticationUseCase>(relaxed = true)
    private val settingsUseCase = mockk<SettingsUseCase>(relaxed = true)
    private val serverUrl = ServerUrl("",
        isUrlValid = false,
        allowSaveUrl = false,
        isLoggingEnabled = false,
        showUnsecureServerConfirmation = false
    )
    private val underTest = ServerActionsContext(
        serverUseCase,
        authenticationUseCase,
        settingsUseCase,
    )
    private val validHttpsUrl = "https://valid.url.com"
    private val validHttpUrl = "http://valid.url.com"
    private val oldServerUrl = "https://old.url.com"

    @Before
    fun setUp() {
        every { serverUseCase.getServerUrl() } returns oldServerUrl
    }

    @Test
    fun `allows valid server url`() = runTest {
        handle(
            AttemptChangeServerUrlTo(
            validHttpsUrl
        )
        ).test {
            awaitComplete()
        }

        coVerify { serverUseCase.setServerUrl(validHttpsUrl) }
    }

    @Test
    fun `verifies the user wants to use a valid but unsecured server url`() = runTest {
        handle(
            AttemptChangeServerUrlTo(
            validHttpUrl
        )
        ).test {
            assert(awaitItem() == ShowUnsecureServerConfirmation)
            awaitComplete()
        }

        coVerify { serverUseCase wasNot Called }
    }

    @Test
    fun `allows use of insecure but valid password when the user accepts it`() = runTest {
        handle(
            ChangeServerUrlTo(
            validHttpUrl
        )
        ).test {
            assert(awaitItem() == HideUnsecureServerConfirmation)
            awaitComplete()
        }

        coVerify { serverUseCase.setServerUrl(validHttpUrl) }
    }

    @Test
    fun `shows that https url is valid`() = runTest {
        handle(UrlTyped(validHttpsUrl)).test {
            assert(awaitItem() == ShowUrlValidation(oldServerUrl, true))
            awaitComplete()
        }
    }

    @Test
    fun `shows that http url is valid`() = runTest {
        handle(UrlTyped(validHttpUrl)).test {
            assert(awaitItem() == ShowUrlValidation(oldServerUrl, true))
            awaitComplete()
        }
    }

    @Test
    fun `shows that invalid url is invalid`() = runTest {
        handle(UrlTyped("invalid url")).test {
            assert(awaitItem() == ShowUrlValidation(oldServerUrl, false))
            awaitComplete()
        }
    }

    @Test
    fun `shows that ip address is valid`() = runTest {
        handle(UrlTyped("127.0.0.1")).test {
            assert(awaitItem() == ShowUrlValidation(oldServerUrl, true))
            awaitComplete()
        }
    }

    @Test
    fun `shows that local dns url is valid`() = runTest {
        handle(UrlTyped("serverlocal")).test {
            assert(awaitItem() == ShowUrlValidation(oldServerUrl, true))
            awaitComplete()
        }
    }

    @Test
    fun `shows that local dns url with non standard TLD is valid`() = runTest {
        handle(UrlTyped("server.local")).test {
            assert(awaitItem() == ShowUrlValidation(oldServerUrl, true))
            awaitComplete()
        }
    }

    private fun handle(action: ServerAction) =
        underTest.handleAction(serverUrl.copy(
            prefilledUrl = "",
            isUrlValid = false,
        ), action) {}

}