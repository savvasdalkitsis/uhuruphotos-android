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

import android.content.Context
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.DisplayingDateFormat
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.DisplayingDateTimeFormat
import com.savvasdalkitsis.uhuruphotos.foundation.date.api.module.DateModule.DisplayingTimeFormat
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import dagger.hilt.android.qualifiers.ApplicationContext
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

class DateDisplayer @Inject constructor(
    private val dateParser: DateParser,
    @DisplayingDateFormat
    private val displayingDateFormat: DateTimeFormatter,
    @DisplayingDateTimeFormat
    private val displayingDateTimeFormat: DateTimeFormatter,
    @DisplayingTimeFormat
    private val displayingTimeFormat: DateTimeFormatter,
    @ApplicationContext
    private val context: Context,
) {

    fun dateString(date: String?): String = format(date, displayingDateFormat, appendTimeIfToday = false)

    fun dateTimeString(date: String?): String = format(date, displayingDateTimeFormat, appendTimeIfToday = true)

    private fun format(date: String?, formatter: DateTimeFormatter, appendTimeIfToday: Boolean) =
        dateParser.parseDateOrTimeString(date)?.let {
            format(it, formatter, appendTimeIfToday)
        } ?: ""

    private fun format(date: DateTime, formatter: DateTimeFormatter, appendTimeIfToday: Boolean): String =
        if (DateUtils.isToday(date)) {
            context.getString(string.today) + if (appendTimeIfToday) {
                " " + displayingTimeFormat.print(date.toLocalDateTime())
            } else {
                ""
            }
        } else {
            formatter.print(date.toLocalDateTime())
        }
}
