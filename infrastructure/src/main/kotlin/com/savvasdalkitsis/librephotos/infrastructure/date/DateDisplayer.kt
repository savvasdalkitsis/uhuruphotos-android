package com.savvasdalkitsis.librephotos.infrastructure.date

import android.text.format.DateUtils
import com.savvasdalkitsis.librephotos.infrastructure.log.log
import com.savvasdalkitsis.librephotos.infrastructure.module.InfrastructureModule
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class DateDisplayer @Inject constructor(
    @InfrastructureModule.ParsingDateFormat
    private val parsingDateFormat: DateFormat,
    @InfrastructureModule.ParsingDateTimeFormat
    private val parsingDateTimeFormat: DateFormat,
    @InfrastructureModule.DisplayingDateFormat
    private val displayingDateFormat: DateFormat,
    @InfrastructureModule.DisplayingDateTimeFormat
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