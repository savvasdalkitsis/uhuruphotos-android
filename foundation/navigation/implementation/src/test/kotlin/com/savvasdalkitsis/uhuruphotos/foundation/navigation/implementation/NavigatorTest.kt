package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

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
        intentLauncher = mockk()
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

    private fun TestScope.givenRoutes(vararg routes: NavigationRoute) {
        routes.forEach {
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