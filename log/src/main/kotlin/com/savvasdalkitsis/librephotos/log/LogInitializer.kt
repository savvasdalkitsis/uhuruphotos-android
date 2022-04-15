package com.savvasdalkitsis.librephotos.log

import com.orhanobut.logger.LogAdapter
import com.orhanobut.logger.Logger
import javax.inject.Inject

class LogInitializer @Inject constructor(
    private val logAdapter: LogAdapter,
) {

    fun initialize() {
        Logger.addLogAdapter(logAdapter)
    }
}