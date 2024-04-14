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
package com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.ui.state

import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaDay
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.CountryVisit
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.DayOfMonth
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.DayOfWeek
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Month
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Year
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class StatsState(
    val isLoadingMediaByYear: Boolean = true,
    val isLoadingMediaByMonth: Boolean = true,
    val isLoadingMediaByDayOfMonth: Boolean = true,
    val isLoadingMediaByDayOfWeek: Boolean = true,
    val isLoadingMediaHeatMap: Boolean = true,
    val mediaByYear: Map<Year, Int> = emptyMap(),
    val mediaByMonth: Map<Month, Int> = emptyMap(),
    val mediaByDayOfMonth: Map<DayOfMonth, Int> = emptyMap(),
    val mediaByDayOfWeek: Map<DayOfWeek, Int> = emptyMap(),
    val mediaHeatMap: Map<MediaDay, Int> = emptyMap(),
    val isLoadingTimeline: Boolean = true,
    val timeline: ImmutableList<CountryVisit> = persistentListOf(),
    val photoCount: Int? = null,
    val videoCount: Int? = null,
) {
    val isLoading = isLoadingMediaByYear
            && isLoadingTimeline
            && isLoadingMediaByMonth
            && isLoadingMediaByDayOfMonth
            && isLoadingMediaByDayOfWeek
            && isLoadingMediaHeatMap
}