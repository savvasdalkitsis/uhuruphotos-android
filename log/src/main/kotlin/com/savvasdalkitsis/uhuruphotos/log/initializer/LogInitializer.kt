package com.savvasdalkitsis.uhuruphotos.log.initializer

import com.savvasdalkitsis.uhuruphotos.initializer.ApplicationCreated
import timber.log.Timber
import javax.inject.Inject

class LogInitializer @Inject constructor(
) : ApplicationCreated {

    override fun onAppCreated() {
        Timber.plant(Timber.DebugTree())
    }
}