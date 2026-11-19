/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.actions

import com.savvasdalkitsis.uhuruphotos.feature.auth.domain.api.cookies.CookieMonitor
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.seam.WebLoginActionsContext
import com.savvasdalkitsis.uhuruphotos.feature.auth.view.implementation.ui.WebLoginState
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.Navigator
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoadPageTest {

    private val navigator = mockk<Navigator>(relaxed = true)
    private val authCookie = CompletableDeferred<Unit>()
    private val context = WebLoginActionsContext(
        cookieMonitor = CookieMonitor { context -> CoroutineScope(context).launch { authCookie.await() } },
        navigator = navigator,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `leaving the login page before logging in does not navigate up again`() = runTest {
        val loading = launch { LoadPage("https://auth/login").run { context.handle(WebLoginState("")) }.collect() }
        runCurrent()

        loading.cancel()
        runCurrent()

        verify(exactly = 0) { navigator.navigateUp() }
    }

    @Test
    fun `leaves the login page once the auth cookie arrives`() = runTest {
        val loading = launch { LoadPage("https://auth/login").run { context.handle(WebLoginState("")) }.collect() }
        runCurrent()

        authCookie.complete(Unit)
        loading.join()

        verify(exactly = 1) { navigator.navigateUp() }
    }
}
