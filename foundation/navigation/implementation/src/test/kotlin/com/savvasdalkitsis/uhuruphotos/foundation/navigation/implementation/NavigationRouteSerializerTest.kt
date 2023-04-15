package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation

import android.os.Bundle
import com.google.gson.Gson
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.api.NavigationRoute
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.Base64String
import com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization.Base64Transcoder
import io.mockk.every
import io.mockk.mockk
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

data class NamedRoute(val field: String) : NavigationRoute
data class FieldsRoute(
    val field3: Boolean,
    val field1: String,
    val field2: Int,
) : NavigationRoute
object NoFieldsObject : NavigationRoute