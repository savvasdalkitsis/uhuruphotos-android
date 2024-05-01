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
package com.savvasdalkitsis.uhuruphotos.feature.stats.domain.implementation.usecase

import android.content.Context
import android.location.Geocoder
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaDay
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaItem
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.CountryVisit
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.DayOfMonth
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.DayOfWeek
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Month
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Year
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.usecase.StatsUseCase

class StatsUseCase(
    val context: Context,
) : StatsUseCase {

    override fun List<MediaItem>.breakdownByTypeIsVideo(): Map<Boolean, List<MediaItem>> =
        groupBy { it.id.isVideo }

    override fun List<MediaItem>.breakdownByYear(): Map<Year, Int> = breakdownBy(
        grouper =  { it.mediaDay?.year },
        mapper = { Year(it) },
    )

    override fun List<MediaItem>.breakdownByMonth(): Map<Month, Int> = breakdownBy(
        grouper =  { it.mediaDay?.month },
        mapper = { Month(it) },
    )

    override fun List<MediaItem>.breakdownByDayOfMonth(): Map<DayOfMonth, Int> = breakdownBy(
        grouper = { it.mediaDay?.day },
        mapper = { DayOfMonth(it) },
    )

    override fun List<MediaItem>.breakdownByDayOfWeek(): Map<DayOfWeek, Int> = breakdownBy(
        grouper = { it.mediaDay?.dayOfWeek },
        mapper = { DayOfWeek(it) },
    )

    override fun List<MediaItem>.breakdownByMediaDay(): Map<MediaDay, Int> = breakdownBy(
        grouper = { it.mediaDay },
        mapper = { it },
    )

    private fun <T, P>List<MediaItem>.breakdownBy(grouper: (MediaItem) -> T?, mapper: (T) -> P): Map<P, Int> =
        groupBy(grouper).mapNotNull {
            it.key?.let { item ->
                mapper(item) to it.value.count()
            }
        }.toMap()

    override fun List<MediaItem>.timeline(): Result<List<CountryVisit>, Unit> =
        when {
            !Geocoder.isPresent() -> Err(Unit)
            else -> {
                val geocoder = Geocoder(context)
                Ok(sortedBy { it.sortableDate }
                    .mapNotNull { item ->
                        item.mediaDay?.let { day ->
                            item.latLng?.let { (lat, lon) ->
                                @Suppress("DEPRECATION")
                                geocoder.getFromLocation(lat, lon, 1)?.getOrNull(0)?.let { location ->
                                    day to location.countryName
                                }
                            }
                        }
                    }.fold(emptyList()) { acc, (day, country) ->
                        val lastVisit = acc.lastOrNull()
                        when {
                            lastVisit == null -> listOf(CountryVisit(day, day, country))
                            lastVisit.country != country -> acc + CountryVisit(day, day, country)
                            else -> acc.dropLast(1) + CountryVisit(lastVisit.startDate, day, country)
                        }
                    }
                )
            }
        }

}