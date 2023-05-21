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
package com.savvasdalkitsis.uhuruphotos.foundation.preferences.implementation

import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.NullableSerializer
import com.fredporciuncula.flow.preferences.Preference
import com.fredporciuncula.flow.preferences.Serializer
import com.savvasdalkitsis.uhuruphotos.foundation.preferences.api.Preferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Preferences @Inject constructor(
    private val flowSharedPreferences: FlowSharedPreferences,
) : Preferences {

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        bool(key, defaultValue).get()

    override fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean> =
        bool(key, defaultValue).asFlow()

    override fun setBoolean(key: String, value: Boolean) {
        bool(key, value).set(value)
    }

    override fun getInt(key: String, defaultValue: Int): Int =
        int(key, defaultValue).get()

    override fun getNullableInt(key: String, defaultValue: Int?): Int? =
        nullableString(key, defaultValue?.toString()).get()?.toIntOrNull()

    override fun observeInt(key: String, defaultValue: Int): Flow<Int> =
        int(key, defaultValue).asFlow()

    override fun setInt(key: String, value: Int) {
        int(key, value).set(value)
    }

    override fun getString(key: String, defaultValue: String): String =
        string(key, defaultValue).get()

    override fun getNullableString(key: String, defaultValue: String?): String? =
        nullableString(key, defaultValue).get()

    override fun setString(key: String, value: String) {
        string(key, "").set(value)
    }

    override fun getStringSet(key: String, defaultValue: Set<String>): Set<String> =
        stringSet(key, defaultValue).get()

    override fun observeStringSet(key: String, defaultValue: Set<String>): Flow<Set<String>> =
        stringSet(key, defaultValue).asFlow()

    override fun setStringSet(key: String, value: Set<String>) {
        stringSet(key, value).set(value)
    }

    override fun observeNullableString(key: String, defaultValue: String?): Flow<String?> =
        nullableString(key, defaultValue).asFlow()

    override fun observeString(key: String, defaultValue: String): Flow<String> =
        string(key, defaultValue).asFlow()

    override fun <T : Enum<T>> getEnum(key: String, defaultValue: T, factory: (String) -> T): T =
        enum(key, defaultValue, factory).get()

    override fun <T : Enum<T>> getEnumNullable(key: String, defaultValue: T?, factory: (String) -> T): T? =
        enum(key, defaultValue, factory).get()

    override fun <T : Enum<T>> observeEnum(key: String, defaultValue: T, factory: (String) -> T): Flow<T> =
        enum(key, defaultValue, factory).asFlow()

    override fun <T : Enum<T>> observeEnumNullable(key: String, defaultValue: T?, factory: (String) -> T): Flow<T?> =
        enum(key, defaultValue, factory).asFlow()

    override fun <T : Enum<T>> setEnum(key: String, value: T, factory: (String) -> T) {
        enum(key, value, factory).set(value)
    }

    private fun int(key: String, defaultValue: Int) =
        flowSharedPreferences.getInt(key, defaultValue)

    private fun string(key: String, defaultValue: String) =
        flowSharedPreferences.getString(key, defaultValue)

    private fun bool(key: String, defaultValue: Boolean) =
        flowSharedPreferences.getBoolean(key, defaultValue)

    private fun nullableString(key: String, defaultValue: String?) =
        flowSharedPreferences.getNullableString(key, defaultValue)

    private fun <T: Enum<T>> enum(key: String, defaultValue: T, factory: (String) -> T): Preference<T> {
        val serializer = object : Serializer<T> {
            override fun deserialize(serialized: String) = factory(serialized)
            override fun serialize(value: T) = value.name
        }
        return flowSharedPreferences.getObject(key, serializer, defaultValue)
    }

    @JvmName("enumNullable")
    private fun <T: Enum<T>> enum(key: String, defaultValue: T?, factory: (String) -> T?): Preference<T?> {
        val serializer = object : NullableSerializer<T> {
            override fun deserialize(serialized: String?) = serialized?.let(factory)
            override fun serialize(value: T?) = value?.name
        }
        return flowSharedPreferences.getNullableObject(key, serializer, defaultValue)
    }

    private fun stringSet(
        key: String,
        value: Set<String>
    ) = flowSharedPreferences.getStringSet(key, value)
}