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
package com.savvasdalkitsis.uhuruphotos.foundation.date.api

import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DateParserTest {

    private val module = DateModule()
    private val underTest = DateParser(module.parsingDateFormat(), module.parsingDateTimeFormat())

    @Test
    fun `parses a date`() {
        assertEquals(DateTime(2024, 3, 9, 0, 0), underTest.parseDateOrTimeString("2024-03-09"))
    }

    @Test
    fun `parses a date time`() {
        assertEquals(DateTime(2024, 3, 9, 14, 5, 7), underTest.parseDateOrTimeString("2024-03-09T14:05:07Z"))
    }

    @Test
    fun `does not parse anything else`() {
        assertNull(underTest.parseDateOrTimeString(null))
        assertNull(underTest.parseDateOrTimeString(""))
        assertNull(underTest.parseDateOrTimeString("2024/03/09 14:05:07"))
        assertNull(underTest.parseDateTimeString("2024-03-09"))
        assertNull(underTest.parseDateString("2024-03-09T14:05:07Z"))
    }

    @Test
    fun `parses date times without throwing away an exception each time`() {
        underTest.parseDateOrTimeString("2024-03-09T14:05:07Z")
        val before = allocatedBytes()

        repeat(1_000) {
            underTest.parseDateOrTimeString("2024-03-09T14:05:07Z")
            underTest.parseDateOrTimeString(null)
        }

        val allocated = allocatedBytes() - before
        assertTrue("allocated ${allocated / 1024} KB", allocated < 1024 * 1024)
    }

    private fun allocatedBytes(): Long {
        val threads = Class.forName("java.lang.management.ManagementFactory").getMethod("getThreadMXBean").invoke(null)
        return Class.forName("com.sun.management.ThreadMXBean")
            .getMethod("getCurrentThreadAllocatedBytes").invoke(threads) as Long
    }
}
