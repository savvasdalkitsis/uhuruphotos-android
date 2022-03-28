package com.savvasdalkitsis.librephotos.date

import android.text.format.DateUtils
import java.text.DateFormat
import javax.inject.Inject

class DateDisplayer @Inject constructor(
    private val dateFormat: DateFormat,
) {

    fun dateString(albumDate: String?): String {
        val now = System.currentTimeMillis()
        return when (albumDate) {
            null -> ""
            else -> try {
                when (val millis = dateFormat.parse(albumDate)) {
                    null -> ""
                    else -> DateUtils.getRelativeTimeSpanString(
                        millis.time,
                        now,
                        DateUtils.DAY_IN_MILLIS
                    ).toString()
                }
            } catch (e: Exception) {
                albumDate
            }
        }
    }
}