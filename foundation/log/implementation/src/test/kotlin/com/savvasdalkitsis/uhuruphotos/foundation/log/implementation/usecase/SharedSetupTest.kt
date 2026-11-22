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
package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation.usecase

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SharedSetupTest {

    @get:Rule
    val temp = TemporaryFolder()

    @Test
    fun `remaps timestamped and older log lines for the log viewer`() {
        val logs = temp.newFolder("logs")
        File(logs, "log.after_1000.txt").writeText(
            "W/Tag: old line\n2026-11-22T08:21:03.408123Z I/Test: new line\n",
        )

        val remapped = SharedSetup(logs, File(temp.root, "remapped")).getLatestLogFiles()!!.readLines()

        assertEquals(
            listOf(
                "[W] 2000-01-01 00:00:00.000 [Tag.kt:0]: old line",
                "[I] 2026-11-22 08:21:03.000 [Test.kt:0]: new line",
            ),
            remapped,
        )
    }
}
