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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.viewmodel

import com.savvasdalkitsis.uhuruphotos.foundation.log.api.Log as LogSwitch
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import saschpe.log4k.Log
import saschpe.log4k.Logger

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {

    private val messages = mutableListOf<String>()
    private val recorder = object : Logger() {
        override fun print(level: Log.Level, tag: String, message: String?, throwable: Throwable?) {
            message?.let { messages += it }
        }
    }
    private val previousLoggers = Log.loggers.toList()
    private val wasEnabled = LogSwitch.enabled

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Log.loggers.clear()
        Log.loggers += recorder
        LogSwitch.enabled = true
    }

    @After
    fun tearDown() {
        LogSwitch.enabled = wasEnabled
        Log.loggers.clear()
        Log.loggers += previousLoggers
        Dispatchers.resetMain()
    }

    @Test
    fun `logs state updates by name instead of by content`() {
        object : NavigationViewModel<LibraryState, Unit, NavigationRoute>({ _, _ -> emptyFlow() }, LibraryState()) {
            override fun onRouteSet(route: NavigationRoute) = Unit
        }

        assertEquals(emptyList<String>(), messages.filter { "every photo in the library" in it })
        assertTrue(messages.any { "LibraryState" in it })
    }
}

private class LibraryState {
    override fun toString() = "every photo in the library"
}
