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
package com.savvasdalkitsis.uhuruphotos.foundation.log.api

import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import saschpe.log4k.Logger
import saschpe.log4k.Log as Log4k

class LogTest {

    private val previousLoggers = Log4k.loggers.toList()
    private val wasEnabled = Log.enabled

    @Before
    fun failEveryWrite() {
        Log4k.loggers.clear()
        Log4k.loggers += object : Logger() {
            override fun print(level: Log4k.Level, tag: String, message: String?, throwable: Throwable?) {
                throw IllegalStateException("disk full")
            }
        }
    }

    @After
    fun restore() {
        Log4k.loggers.clear()
        Log4k.loggers += previousLoggers
        Log.enabled = wasEnabled
    }

    @Test
    fun `leaves logging disabled after a failed warning`() {
        Log.enabled = false

        log(IllegalArgumentException("warning"))
        log(IllegalArgumentException("warning")) { "message" }
        logError(IllegalArgumentException("error"))

        assertFalse(Log.enabled)
    }
}
