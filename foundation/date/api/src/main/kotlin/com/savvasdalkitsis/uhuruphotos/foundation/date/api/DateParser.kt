/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.date.api

import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateFormat
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.ParsingDateTimeFormat
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

class DateParser @Inject constructor(
    @ParsingDateFormat
    private val parsingDateFormat: DateTimeFormatter,
    @ParsingDateTimeFormat
    private val parsingDateTimeFormat: DateTimeFormatter,
) {

    fun parseDateOrTimeString(dateOrTime: String?) =
        parseDateString(dateOrTime) ?: parseDateTimeString(dateOrTime)

    fun parseDateString(date: String?) =  parseString(date, parsingDateFormat)

    fun parseDateTimeString(date: String?) =  parseString(date, parsingDateTimeFormat)

    private fun parseString(dateString: String?, parse: DateTimeFormatter): DateTime? = try {
        parse.parseDateTime(dateString)
    } catch (e: Exception) {
        null
    }
}