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
package com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.usecase

import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaDay
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.CountryVisit
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.DayOfMonth
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.DayOfWeek
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Month
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Year

interface StatsUseCase {

    fun List<MediaItem>.breakdownByTypeIsVideo(): Map<Boolean, List<MediaItem>>
    fun List<MediaItem>.breakdownByYear(): Map<Year, Int>
    fun List<MediaItem>.breakdownByMonth(): Map<Month, Int>
    fun List<MediaItem>.breakdownByDayOfMonth(): Map<DayOfMonth, Int>
    fun List<MediaItem>.timeline(): Result<List<CountryVisit>, Unit>
    fun List<MediaItem>.breakdownByDayOfWeek(): Map<DayOfWeek, Int>
    fun List<MediaItem>.breakdownByMediaDay(): Map<MediaDay, Int>
}