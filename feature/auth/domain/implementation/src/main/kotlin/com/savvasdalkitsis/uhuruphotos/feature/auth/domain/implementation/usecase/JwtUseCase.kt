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
package com.savvasdalkitsis.uhuruphotos.feature.auth.domain.implementation.usecase

import android.util.Base64.NO_PADDING
import android.util.Base64.NO_WRAP
import android.util.Base64.URL_SAFE
import android.util.Base64.decode
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.nio.charset.Charset
import javax.inject.Inject

class JwtUseCase @Inject constructor() {

    fun String.expired(): Boolean = JWT(this).isExpired(0)

    operator fun String.get(property: String): String {
        val parts: Array<String> = splitToken(this)
        val mapType: Type = object : TypeToken<Map<String, String>>() {}.type
        val payload = Gson().fromJson<Map<String, String>>(base64Decode(parts[1]), mapType)
        return payload[property].toString()
    }

    private fun base64Decode(string: String): String = try {
        val bytes = decode(string, URL_SAFE or NO_WRAP or NO_PADDING)
        String(bytes, Charset.defaultCharset())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException(
            "Received bytes didn't correspond to a valid Base64 encoded string.",
            e
        )
    }

    private fun splitToken(token: String): Array<String> {
        var parts: Array<String> = token.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (parts.size == 2 && token.endsWith(".")) {
            parts = arrayOf(parts[0], parts[1], "")
        }
        if (parts.size != 3) {
            throw IllegalArgumentException(
                String.format(
                    "The token was expected to have 3 parts, but got %s.",
                    parts.size
                )
            )
        }
        return parts
    }

}