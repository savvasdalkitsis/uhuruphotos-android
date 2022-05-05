package com.savvasdalkitsis.uhuruphotos.network.logging

import com.savvasdalkitsis.uhuruphotos.log.log
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class TimberLogger @Inject constructor() : HttpLoggingInterceptor.Logger {

    override fun log(message: String) = log("OkHttp") { message }
}
