/*
Copyright 2026 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.app

import com.bugsnag.android.Bugsnag
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.HANDLED_ERROR_TAG
import saschpe.log4k.Log
import saschpe.log4k.Logger

internal class BugsnagErrorLogger(
    private val notify: (Throwable) -> Unit = { Bugsnag.notify(it) },
) : Logger() {

    override fun print(level: Log.Level, tag: String, message: String?, throwable: Throwable?) {
        if (tag == HANDLED_ERROR_TAG && throwable != null) {
            notify(throwable)
        }
    }
}

internal fun reportHandledErrorsToBugsnag(loggers: MutableList<Logger> = Log.loggers) {
    if (loggers.none { it is BugsnagErrorLogger }) {
        loggers.add(0, BugsnagErrorLogger())
    }
}
