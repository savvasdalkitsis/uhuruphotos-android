package com.savvasdalkitsis.librephotos.infrastructure.log

import com.orhanobut.logger.Logger

fun log(msg: String, tag: String = "") {
    Logger.log(Logger.VERBOSE, tag, msg, null)
}

fun log(t: Throwable) {
    Logger.log(Logger.WARN, "", t.message, t)
}