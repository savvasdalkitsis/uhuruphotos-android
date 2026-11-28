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

class BackupRulesTest {

    private val deviceBoundPreferences = setOf("sharedpref" to "encrypted_shared_prefs.xml")

    private fun rules(name: String) = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(File("src/main/res/xml/$name.xml"))
        .documentElement

    private fun manifestApplication() = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(File("src/main/AndroidManifest.xml"))
        .getElementsByTagName("application").item(0) as Element

    private fun Element.child(name: String) =
        (0 until childNodes.length).map(childNodes::item).filterIsInstance<Element>().filter { it.tagName == name }

    private fun Element.excluded() = child("exclude").map { it.getAttribute("domain") to it.getAttribute("path") }.toSet()

    @Test
    fun `keeps the encrypted preferences out of cloud backups`() {
        val cloudBackup = rules("data_extraction").child("cloud-backup").single()

        assertEquals(deviceBoundPreferences, cloudBackup.excluded())
    }

    @Test
    fun `keeps the encrypted preferences out of device transfers`() {
        val deviceTransfer = rules("data_extraction").child("device-transfer").single()

        assertEquals(deviceBoundPreferences, deviceTransfer.excluded())
    }

    @Test
    fun `keeps the encrypted preferences out of backups before android 12`() {
        val application = manifestApplication()
        val fullBackupContent = application.getAttribute("android:fullBackupContent").removePrefix("@xml/")

        assertEquals("full-backup-content", rules(fullBackupContent).tagName)
        assertEquals(deviceBoundPreferences, rules(fullBackupContent).excluded())
        assertEquals("@xml/data_extraction", application.getAttribute("android:dataExtractionRules"))
    }
}
