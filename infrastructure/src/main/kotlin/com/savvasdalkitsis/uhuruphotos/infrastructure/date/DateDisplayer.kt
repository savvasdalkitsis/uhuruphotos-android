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
package com.savvasdalkitsis.uhuruphotos.infrastructure.date

import android.text.format.DateUtils
import com.savvasdalkitsis.uhuruphotos.infrastructure.module.InfrastructureModule.DisplayingDateFormat
import com.savvasdalkitsis.uhuruphotos.infrastructure.module.InfrastructureModule.DisplayingDateTimeFormat
import com.savvasdalkitsis.uhuruphotos.infrastructure.module.InfrastructureModule.ParsingDateFormat
import com.savvasdalkitsis.uhuruphotos.infrastructure.module.InfrastructureModule.ParsingDateTimeFormat
import com.savvasdalkitsis.uhuruphotos.log.log
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class DateDisplayer @Inject constructor(
    @ParsingDateFormat
    private val parsingDateFormat: DateFormat,
    @ParsingDateTimeFormat
    private val parsingDateTimeFormat: DateFormat,
    @DisplayingDateFormat
    private val displayingDateFormat: DateFormat,
    @DisplayingDateTimeFormat
    private val displayingDateTimeFormat: DateFormat,
) {

    fun dateString(date: String?): String = formatString(date, parsingDateFormat, displayingDateFormat)

    fun dateTimeString(date: String?): String = formatString(date, parsingDateTimeFormat, displayingDateTimeFormat)

    private fun formatString(dateString: String?, parse: DateFormat, display: DateFormat): String = when (dateString) {
        null -> ""
        else -> try {
            when (val date = parse.parse(dateString)) {
                null -> ""
                else -> format(date, display)
            }
        } catch (e: Exception) {
            log(e)
            dateString
        }
    }

    private fun format(date: Date, formatter: DateFormat): String = if (DateUtils.isToday(date.time)) {
        "Today"
    } else {
        formatter.format(date)
    }
}