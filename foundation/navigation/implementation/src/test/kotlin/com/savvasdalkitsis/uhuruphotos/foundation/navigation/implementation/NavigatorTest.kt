/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

import com.savvasdalkitsis.uhuruphotos.feature.home.view.api.navigation.HomeNavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.singleTop
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigatorTest {

    private val underTest = Navigator(
        context = mockk(),
        intentLauncher = mockk(),
    )

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `adds new route in singleTop when not existing`() = runTest {
        givenRoutes(A(), B())

        underTest.singleTop(C())

        advanceUntilIdle()

        thenRoutesAre(A(), B(), C() )
    }

    @Test
    fun `does not replace existing route if at the top`() = runTest {
        givenRoutes(A(), B())

        underTest.singleTop(B())

        thenRoutesAre(A(), B())
    }

    @Test
    fun `drops routes above the singleTop route`() = runTest {
        givenRoutes(A(), B(), C())

        underTest.singleTop(B())

        thenRoutesAre(A(), B())
    }

    @Test
    fun `replaces existing route type if at the top but with different data`() = runTest {
        givenRoutes(A(), B())

        underTest.singleTop(B("!"))

        thenRoutesAre(A(), B("!"))
    }

    @Test
    fun `drops routes above the singleTop route and replaces it if data are different`() = runTest {
        givenRoutes(A(), B(), C())

        underTest.singleTop(B("!"))

        thenRoutesAre(A(), B("!"))
    }

    @Test
    fun `navigating up twice in one frame from a second screen lands home`() = runTest {
        givenRoutes(A(), B())

        underTest.navigateUp()
        underTest.navigateUp()
        advanceUntilIdle()

        assertEquals(HomeNavigationRoute, underTest.backStack.last())
    }

    @Test
    fun `navigating up from the only screen lands home`() = runTest {
        givenRoutes(A())

        underTest.navigateUp()

        thenRoutesAre(HomeNavigationRoute)
    }

    private fun TestScope.givenRoutes(vararg routes: NavigationRoute) {
        underTest.newRoot(routes.first())
        routes.drop(1).forEach {
            underTest.navigateTo(it)
        }
        advanceUntilIdle()
    }

    private fun TestScope.thenRoutesAre(vararg expectedRoutes: NavigationRoute) {
        advanceUntilIdle()
        assertEquals(expectedRoutes.toList(), underTest.backStack.toList())
    }
}

data class A(
    val name: String = "routeA"
) : NavigationRoute

data class B(
    val name: String = "routeB"
) : NavigationRoute

data class C(
    val name: String = "routeC"
) : NavigationRoute