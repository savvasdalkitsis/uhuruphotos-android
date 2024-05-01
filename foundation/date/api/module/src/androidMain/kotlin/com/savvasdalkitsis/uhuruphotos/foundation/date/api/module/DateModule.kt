/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.date.api.module

import com.savvasdalkitsis.uhuruphotos.foundation.android.api.module.AndroidModule
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateDisplayer
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.DateParser
import com.savvasdalkitsis.uhuruphotos.foundation.date.implementation.initializer.DateInitializer
import com.savvasdalkitsis.uhuruphotos.foundation.initializer.api.ApplicationCreated
import com.savvasdalkitsis.uhuruphotos.foundation.inject.api.singleInstance
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

object DateModule {

    val parsingDateFormat: DateTimeFormatter get() = DateTimeFormat.forPattern("yyyy-MM-dd")

    val parsingDateTimeFormat: DateTimeFormatter get() = DateTimeFormat.forPattern("yyyy-MM-dd'T'kk:mm:ss'Z'")

    val displayingDateFormat: DateTimeFormatter get() = DateTimeFormat.fullDate()

    val displayingDateTimeFormat: DateTimeFormatter get() = DateTimeFormat.fullDateTime()

    val displayingTimeFormat: DateTimeFormatter get() = DateTimeFormat.fullTime()

    val dateParser: DateParser
        get() = DateParser(
            parsingDateFormat = parsingDateFormat,
            parsingDateTimeFormat = parsingDateTimeFormat
        )

    val dateDisplayer: DateDisplayer
        get() = DateDisplayer(
            dateParser = dateParser,
            displayingDateFormat = displayingDateFormat,
            displayingDateTimeFormat = displayingDateTimeFormat,
            displayingTimeFormat = displayingTimeFormat,
            context = AndroidModule.applicationContext
        )

    val dateInitializer: ApplicationCreated by singleInstance {
        DateInitializer()
    }
}