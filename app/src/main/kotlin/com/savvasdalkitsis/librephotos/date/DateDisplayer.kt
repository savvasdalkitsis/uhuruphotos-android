package com.savvasdalkitsis.librephotos.date

import android.text.format.DateUtils
import com.savvasdalkitsis.librephotos.module.Module
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

class DateDisplayer @Inject constructor(
    @Module.ParsingDateFormat
    private val parsingDateFormat: DateFormat,
    @Module.DisplayingDateFormat
    private val displayingDateFormat: DateFormat,
) {

    fun dateString(albumDate: String?): String {
        return when (albumDate) {
            null -> ""
            else -> try {
                when (val date = parsingDateFormat.parse(albumDate)) {
                    null -> ""
                    else -> format(date)
                }
            } catch (e: Exception) {
                albumDate
            }
        }
    }

    private fun format(date: Date): String = if (DateUtils.isToday(date.time)) {
        "Today"
    } else {
        displayingDateFormat.format(date)
    }
}