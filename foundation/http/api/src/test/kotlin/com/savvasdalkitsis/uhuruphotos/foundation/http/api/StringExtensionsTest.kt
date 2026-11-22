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
package com.savvasdalkitsis.uhuruphotos.foundation.http.api

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `accepts urls and domains with a valid port`() {
        assertTrue("https://photos.example.com:8443".isValidUrlOrDomain)
        assertTrue("photos.example.com:8443".isValidUrlOrDomain)
    }

    @Test
    fun `rejects urls and domains with a port out of range`() {
        assertFalse("https://photos.example.com:99999".isValidUrlOrDomain)
        assertFalse("photos.example.com:99999".isValidUrlOrDomain)
    }
}
