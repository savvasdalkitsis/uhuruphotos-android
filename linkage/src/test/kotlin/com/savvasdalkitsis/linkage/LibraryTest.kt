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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LibraryTest {

    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun `reads coordinates and paths and skips blank lines`() {
        val file = folder.newFile()
        file.writeText("group:api:2.0\tC:\\libs\\api.jar\n\nplatform\t/sdk/android.jar")

        val libraries = Library.readAll(file)

        assertEquals(
            listOf(Library("group:api:2.0", File("C:\\libs\\api.jar")), Library("platform", File("/sdk/android.jar"))),
            libraries,
        )
        assertEquals("group:api", libraries[0].module)
        assertFalse(libraries[0].isPlatform)
        assertTrue(libraries[1].isPlatform)
    }

    @Test
    fun `only links classes outside META-INF`() {
        assertTrue(JarClasses.isLinkedClass("api/Layout.class"))
        assertFalse(JarClasses.isLinkedClass("META-INF/versions/9/api/Layout.class"))
        assertFalse(JarClasses.isLinkedClass("module-info.class"))
        assertFalse(JarClasses.isLinkedClass("api/layout.properties"))
    }
}
