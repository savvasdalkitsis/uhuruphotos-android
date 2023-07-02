/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

import android.os.Bundle
import com.google.gson.Gson
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.Base64String
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.Base64Transcoder
import io.mockk.every
import io.mockk.mockk
import kotlinx.parcelize.Parcelize
import org.junit.Test
import java.util.*

class NavigationRouteSerializerTest {

    private val underTest = NavigationRouteSerializer(Gson(), object : Base64Transcoder {
        override fun encode(data: String) =
            Base64String(Base64.getEncoder().encodeToString(data.toByteArray()))

        override fun decode(data: Base64String) = String(Base64.getDecoder().decode(data.value))

    })

    @Test
    fun `templates data class with name`() {
        val template = underTest.createRouteTemplateFor(NamedRoute::class)

        assert(template == "NamedRoute/{route}")
    }

    @Test
    fun `templates object`() {
        val template = underTest.createRouteTemplateFor(NoFieldsObject::class)

        assert(template == "NoFieldsObject/{route}")
    }

    @Test
    fun `serializes route to match template`() {
        val serialized = underTest.serialize(
            FieldsRoute(
            field3 = true,
            field1 = "field",
            field2 = 99,
        ))

        assert(serialized.startsWith("FieldsRoute/"))
        assert(serialized != "FieldsRoute/")
    }

    @Test
    fun `serializes and deserializes class from arguments bundle`() {
        val serialized = underTest.serialize(
            FieldsRoute(
            field3 = true,
            field1 = "field",
            field2 = 99,
        ))

        val bundle = mockk<Bundle>(relaxed = true).apply {
            every { getString("route") } returns serialized.removePrefix("FieldsRoute/")
        }

        val result = underTest.deserialize(FieldsRoute::class, bundle)

        assert(result == FieldsRoute(
            field3 = true,
            field1 = "field",
            field2 = 99,
        ))
    }
}

@Parcelize
data class NamedRoute(val field: String) : NavigationRoute
@Parcelize
data class FieldsRoute(
    val field3: Boolean,
    val field1: String,
    val field2: Int,
) : NavigationRoute
@Parcelize
data object NoFieldsObject : NavigationRoute
