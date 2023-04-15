package com.savvasdalkitsis.uhuruphotos.foundation.navigation.implementation.serialization

import android.util.Base64
import javax.inject.Inject

class AndroidBase64Transcoder @Inject constructor(
) : Base64Transcoder {

    override fun encode(data: String): Base64String =
        Base64String(Base64.encodeToString(data.toByteArray(), Base64.DEFAULT))

    override fun decode(data: Base64String): String =
        String(Base64.decode(data.value, Base64.DEFAULT))
}