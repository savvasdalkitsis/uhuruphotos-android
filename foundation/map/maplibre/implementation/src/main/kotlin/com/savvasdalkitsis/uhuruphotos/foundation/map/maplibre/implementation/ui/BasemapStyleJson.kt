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
package com.savvasdalkitsis.uhuruphotos.foundation.map.maplibre.implementation.ui

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import org.maplibre.android.style.layers.LayoutPropertyValue
import org.maplibre.android.style.layers.PropertyValue

internal fun calmStyleJson(json: String, palette: BasemapPalette): String {
    val style = JsonParser.parseString(json).asJsonObject
    style.getAsJsonArray("layers").forEach { it.asJsonObject.calm(palette) }
    return style.toString()
}

private fun JsonObject.calm(palette: BasemapPalette) {
    basemapProperties(get("id").asString, get("type").asString, palette).forEach { property ->
        section(property).add(property.name, property.toJson())
    }
}

private fun JsonObject.section(property: PropertyValue<*>): JsonObject {
    val name = if (property is LayoutPropertyValue) "layout" else "paint"
    return getAsJsonObject(name) ?: JsonObject().also { add(name, it) }
}

private fun PropertyValue<*>.toJson(): JsonElement = when (val value = value) {
    is Number -> JsonPrimitive(value)
    else -> JsonPrimitive(value.toString())
}
