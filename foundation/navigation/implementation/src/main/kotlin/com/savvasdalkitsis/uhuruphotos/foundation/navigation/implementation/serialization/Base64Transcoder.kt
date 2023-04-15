package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization

interface Base64Transcoder {

    fun encode(data: String): Base64String
    fun decode(data: Base64String): String
}

@JvmInline
value class Base64String(val value: String)