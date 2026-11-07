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
package com.savvasdalkitsis.uhuruphotos.app

import org.junit.Assert.assertEquals
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class LocalesConfigTest {

    private val androidNamespace = "http://schemas.android.com/apk/res/android"
    private val stringResources = File("../foundation/strings/api/src/commonMain/composeResources")

    private fun parse(path: String): Element = DocumentBuilderFactory.newInstance()
        .apply { isNamespaceAware = true }
        .newDocumentBuilder()
        .parse(File(path))
        .documentElement

    private fun translatedLocales() = stringResources.listFiles().orEmpty()
        .filter { File(it, "strings.xml").exists() }
        .map { it.name.removePrefix("values").removePrefix("-").replace("-r", "-").ifEmpty { "en" } }
        .toSet()

    private fun declaredLocales(): Set<String> {
        val locales = parse("src/main/res/xml/locales_config.xml").getElementsByTagName("locale")
        return (0 until locales.length)
            .map { (locales.item(it) as Element).getAttributeNS(androidNamespace, "name") }
            .toSet()
    }

    @Test
    fun `declares every language the app is translated to`() {
        assertEquals(translatedLocales(), declaredLocales())
    }

    @Test
    fun `the manifest points to the declared languages`() {
        val application = parse("src/main/AndroidManifest.xml").getElementsByTagName("application").item(0) as Element

        assertEquals("@xml/locales_config", application.getAttributeNS(androidNamespace, "localeConfig"))
    }
}
