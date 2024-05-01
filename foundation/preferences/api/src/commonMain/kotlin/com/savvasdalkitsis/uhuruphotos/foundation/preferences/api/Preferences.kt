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
package com.savvasdalkitsis.uhuruphotos.foundation.preferences.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface Preferences {
    fun remove(key: String)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun observeBoolean(key: String, defaultValue: Boolean): Flow<Boolean>
    fun setBoolean(key: String, value: Boolean)

    fun getInt(key: String, defaultValue: Int): Int
    fun getNullableInt(key: String, defaultValue: Int?): Int?
    fun observeInt(key: String, defaultValue: Int): Flow<Int>
    fun setInt(key: String, value: Int)

    fun getDouble(key: String, defaultValue: Double): Double
    fun getNullableDouble(key: String, defaultValue: Double?): Double?
    fun observeDouble(key: String, defaultValue: Double): Flow<Double>
    fun setDouble(key: String, value: Double)

    fun getFloat(key: String, defaultValue: Float): Float
    fun getNullableFloat(key: String, defaultValue: Float?): Float?
    fun observeFloat(key: String, defaultValue: Float): Flow<Float>
    fun setFloat(key: String, value: Float)

    fun getString(key: String, defaultValue: String): String
    fun getNullableString(key: String, defaultValue: String?): String?
    fun observeString(key: String, defaultValue: String): Flow<String>
    fun observeNullableString(key: String, defaultValue: String?): Flow<String?>
    fun setString(key: String, value: String)

    fun getStringSet(key: String, defaultValue: Set<String>): Set<String>
    fun observeStringSet(key: String, defaultValue: Set<String>): Flow<Set<String>>
    fun setStringSet(key: String, value: Set<String>)

    fun <T : Enum<T>> getEnum(key: String, defaultValue: T, factory: (String) -> T): T
    fun <T : Enum<T>> getEnumNullable(key: String, defaultValue: T?, factory: (String) -> T): T?
    fun <T : Enum<T>> observeEnum(key: String, defaultValue: T, factory: (String) -> T): Flow<T>
    fun <T : Enum<T>> observeEnumNullable(key: String, defaultValue: T?, factory: (String) -> T): Flow<T?>
    fun <T : Enum<T>> setEnum(key: String, value: T, factory: (String) -> T)
}

inline fun <reified T: Enum<T>> Preferences.get(key: String, defaultValue: T): T =
    getEnum(key, defaultValue) {
        enumValueOf(it)
    }

@JvmName("getEnumNullable")
inline fun <reified T: Enum<T>> Preferences.get(key: String, defaultValue: T?): T? =
    getEnumNullable(key, defaultValue) {
        enumValueOf(it)
    }

inline fun <reified T> Preferences.get(key: String, defaultValue: T = null as T): T = when {
    T::class == String::class -> if (defaultValue == null)
        getNullableString(key, null) as T
    else
        getString(key, (defaultValue as? String) ?: "") as T
    T::class == Int::class -> if (defaultValue == null)
        getNullableInt(key, null) as T
    else
        getInt(key, (defaultValue as? Int) ?: 0) as T
    T::class == Float::class -> if (defaultValue == null)
        getNullableFloat(key, null) as T
    else
        getFloat(key, (defaultValue as? Float) ?: 0f) as T
    T::class == Double::class -> if (defaultValue == null)
        getNullableDouble(key, null) as T
    else
        getDouble(key, (defaultValue as? Double) ?: 0.0) as T
    T::class == Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
    else -> throw IllegalArgumentException("Unrecognized preference type requested: ${T::class}")
}

inline fun <reified T: Enum<T>> Preferences.observe(key: String, defaultValue: T): Flow<T> =
    observeEnum(key, defaultValue) {
        enumValueOf(it)
    }

@JvmName("observeEnumNullable")
inline fun <reified T: Enum<T>> Preferences.observe(key: String, defaultValue: T?): Flow<T?> =
    observeEnumNullable(key, defaultValue) {
        enumValueOf(it)
    }

inline fun <reified T> Preferences.observe(key: String, defaultValue: T? = null): Flow<T> = when {
    T::class == String::class -> if (defaultValue == null)
        observeNullableString(key, null).map { it as T }
    else
        observeString(key, (defaultValue as? String) ?: "").map { it as T }
    T::class == Int::class -> observeInt(key, defaultValue as? Int ?: 0).map { it as T }
    T::class == Double::class -> observeDouble(key, defaultValue as? Double ?: 0.0).map { it as T }
    T::class == Float::class -> observeFloat(key, defaultValue as? Float ?: 0f).map { it as T }
    T::class == Boolean::class -> observeBoolean(key, defaultValue as? Boolean ?: false).map { it as T }
    else -> throw IllegalArgumentException("Unrecognized preference type requested: ${T::class}")
}

inline fun <reified T: Enum<T>> Preferences.set(key: String, value: T) {
    setEnum(key, value) {
        enumValueOf(it)
    }
}

inline fun <reified T> Preferences.set(key: String, value: T) = when {
    T::class == String::class -> setString(key, value as String)
    T::class == Int::class -> setInt(key, value as Int)
    T::class == Double::class -> setDouble(key, value as Double)
    T::class == Float::class -> setFloat(key, value as Float)
    T::class == Boolean::class -> setBoolean(key, value as Boolean)
    else -> throw IllegalArgumentException("Unrecognized preference type requested: ${T::class}")
}
