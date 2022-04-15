package com.savvasdalkitsis.librephotos.log.initializer

import com.savvasdalkitsis.librephotos.initializer.ApplicationCreated
import timber.log.Timber
import javax.inject.Inject

class LogInitializer @Inject constructor(
) : ApplicationCreated {

    override fun onAppCreated() {
        Timber.plant(Timber.DebugTree())
    }
}