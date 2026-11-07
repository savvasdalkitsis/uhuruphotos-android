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
package com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaDayModel
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.LocalMediaItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_270
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model.MediaOrientation.ORIENTATION_90
import org.jetbrains.compose.resources.getString
import org.joda.time.DateTime
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.month_january_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_february_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_march_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_april_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_may_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_june_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_july_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_august_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_september_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_october_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_november_short
import uhuruphotos_android.foundation.strings.api.generated.resources.month_december_short

private val shortMonths = listOf(
    string.month_january_short,
    string.month_february_short,
    string.month_march_short,
    string.month_april_short,
    string.month_may_short,
    string.month_june_short,
    string.month_july_short,
    string.month_august_short,
    string.month_september_short,
    string.month_october_short,
    string.month_november_short,
    string.month_december_short,
)

private val rotatedOrientations = setOf(ORIENTATION_90, ORIENTATION_270)

internal suspend fun DateTime.toMediaDay(): MediaDayModel = MediaDayModel(
    day = dayOfMonth,
    dayOfWeek = dayOfWeek,
    month = monthOfYear,
    year = year,
    monthText = getString(shortMonths[monthOfYear - 1]),
)

internal val LocalMediaItem.ratio: Float get() {
    val (w, h) = orientedSize()
    return (w / h.toFloat()).takeIf { it > 0 } ?: 1f
}

private fun LocalMediaItem.orientedSize(): Pair<Int, Int> =
    if (orientation in rotatedOrientations) height to width else width to height
