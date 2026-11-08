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
package com.savvasdalkitsis.linkage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LinkageReportTest {

    private val consumer = Library("group:consumer:1.0", File("consumer.jar"))
    private val missingField = Finding(
        library = consumer,
        fromClass = "consumer/Screen",
        reference = Reference(ReferenceKind.FIELD, "api/Layout", "size", "I"),
        verdict = Verdict.MISSING_MEMBER,
        target = null,
    )
    private val missingClass = missingField.copy(
        reference = Reference(ReferenceKind.CLASS, "api/Gone"),
        verdict = Verdict.MISSING_CLASS,
    )
    private val optional = missingClass.copy(verdict = Verdict.OPTIONAL_PACKAGE)

    @Test
    fun `allowlisted breaks are filtered and stale entries are reported`() {
        val report = LinkageReport(
            listOf(missingField, missingClass, optional),
            setOf("group:consumer -> api/Gone", "group:consumer -> api/Stale"),
        )

        assertEquals(listOf(missingField), report.breaks)
        assertEquals(setOf("group:consumer -> api/Stale"), report.unusedAllowlist)
        assertEquals(mapOf(Verdict.OPTIONAL_PACKAGE to 1), report.noise)
    }

    @Test
    fun `summary names the library, the missing member and stale allowlist entries`() {
        val summary = LinkageReport(listOf(missingField), setOf("group:other -> api/Stale")).summary()

        assertTrue(summary, "1 binary-incompatible references" in summary)
        assertTrue(summary, "group:consumer:1.0: consumer.Screen uses missing field api.Layout.size\n" in summary)
        assertTrue(summary, "allowlist entry no longer needed: group:other -> api/Stale" in summary)
    }

    @Test
    fun `describes missing classes`() {
        assertEquals(
            "group:consumer:1.0: consumer.Screen uses missing class api.Gone",
            missingClass.describe(),
        )
    }
}
