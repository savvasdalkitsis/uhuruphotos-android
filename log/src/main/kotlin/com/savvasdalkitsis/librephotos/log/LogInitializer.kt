package com.savvasdalkitsis.librephotos.log

import timber.log.Timber
import javax.inject.Inject

class LogInitializer @Inject constructor(
) {

    fun initialize() {
        Timber.plant(Timber.DebugTree())
    }
}