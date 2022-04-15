package com.savvasdalkitsis.librephotos.log

import com.orhanobut.logger.LogAdapter

class NoOpLogAdapter : LogAdapter {
    override fun isLoggable(priority: Int, tag: String?): Boolean = false
    override fun log(priority: Int, tag: String?, message: String) {}
}
