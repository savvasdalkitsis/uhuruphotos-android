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

import android.app.Application
import android.content.Context
import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Configuration
import saschpe.log4k.Log
import saschpe.log4k.Logger

internal fun Application.startCrashReporting() {
    startBugsnag(this, Configuration.load(this), { context, configuration -> Bugsnag.start(context, configuration) })
}

internal fun startBugsnag(
    context: Context,
    configuration: Configuration,
    start: (Context, Configuration) -> Unit,
    loggers: MutableList<Logger> = Log.loggers,
) {
    start(context, configuration.apply { groupInstrumentedProcessCrashes() })
    reportHandledErrorsToBugsnag(loggers)
}
