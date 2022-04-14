package com.savvasdalkitsis.librephotos.infrastructure.log

import com.orhanobut.logger.Logger
import com.savvasdalkitsis.librephotos.infrastructure.BuildConfig

fun log(tag: String = "", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        Logger.log(Logger.VERBOSE, tag, msg(), null)
    }
}

fun log(t: Throwable) {
    if (BuildConfig.DEBUG) {
        Logger.log(Logger.WARN, "", t.message, t)
    }
}