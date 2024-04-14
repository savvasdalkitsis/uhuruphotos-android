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
package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlin.reflect.KClass

internal class SealedClassTypeAdapter<T : Any>(
    private val kClass: KClass<Any>,
    private val gson: Gson,
) : TypeAdapter<T>() {

    override fun read(jsonReader: JsonReader): T? {
        jsonReader.beginObject()
        val nextName = jsonReader.nextName()
        val innerClass = kClass.sealedSubclasses.firstOrNull {
            it.simpleName!!.contains(nextName)
        } ?: throw Exception("$nextName is not found to be a data class of the sealed class ${kClass.qualifiedName}")
        val x = gson.fromJson<T>(jsonReader, innerClass.javaObjectType)
        jsonReader.endObject()
        @Suppress("UNCHECKED_CAST")
        return innerClass.objectInstance as T? ?: x
    }

    override fun write(out: JsonWriter, value: T) {
        val jsonString = gson.toJson(value)
        out.beginObject()
        @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        out.name(value.javaClass.canonicalName.splitToSequence(".").last()).jsonValue(jsonString)
        out.endObject()
    }
}
