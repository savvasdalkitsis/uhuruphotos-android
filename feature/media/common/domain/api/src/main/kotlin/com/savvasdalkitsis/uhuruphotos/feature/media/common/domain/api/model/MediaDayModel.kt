/*
Copyright 2023 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.jetbrains.compose.resources.getString
import org.joda.time.DateTime
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.month_april_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_august_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_december_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_february_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_january_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_july_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_june_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_march_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_may_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_november_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_october_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_september_short

@Parcelize
data class MediaDayModel(
    val day: Int,
    val dayOfWeek: Int,
    val month: Int,
    val year: Int,
    val monthText: String,
) : Parcelable {
    val displayText = "$monthText $year"
}

suspend fun DateTime.toMediaDay(): MediaDayModel = MediaDayModel(
    day = dayOfMonth,
    dayOfWeek = dayOfWeek,
    month = monthOfYear,
    year = year,
    monthText = getString(when (monthOfYear) {
        1 -> string.month_january_short
        2 -> string.month_february_short
        3 -> string.month_march_short
        4 -> string.month_april_short
        5 -> string.month_may_short
        6 -> string.month_june_short
        7 -> string.month_july_short
        8 -> string.month_august_short
        9 -> string.month_september_short
        10 -> string.month_october_short
        11 -> string.month_november_short
        else -> string.month_december_short
    })
)
