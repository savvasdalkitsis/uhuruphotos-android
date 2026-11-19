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
package com.savvasdalkitsis.uhuruphotos.foundation.log.implementation

import saschpe.log4k.Log
import saschpe.log4k.Logger
import java.util.concurrent.Executor

internal class BackgroundLogger(
    private val writer: Executor,
    createLogger: () -> Logger,
) : Logger() {

    private val logger by lazy(createLogger)

    override fun print(level: Log.Level, tag: String, message: String?, throwable: Throwable?) {
        writer.execute { logger.log(level, tag, message, throwable) }
    }
}
