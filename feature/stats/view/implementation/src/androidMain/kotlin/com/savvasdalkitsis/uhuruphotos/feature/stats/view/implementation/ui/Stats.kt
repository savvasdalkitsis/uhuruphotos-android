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
package com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.ui

import android.os.Build
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ksurtel.heat_map.HeatMap
import com.ksurtel.heat_map.Properties
import com.ksurtel.heat_map.Record
import com.savvasdalkitsis.uhuruphotos.feature.media.common.domain.api.model.MediaDay
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.DayOfMonth
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.DayOfWeek
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Month
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Year
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.ui.state.StatsState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UpNavButton
import dev.icerock.moko.resources.compose.stringResource
import java.time.LocalDate

@Composable
internal fun Stats(
    state: StatsState,
) {
    CommonScaffold(
        title = { Text(stringResource(strings.stats)) },
        navigationIcon = { UpNavButton() }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding() + 16.dp))
            StatsHeader(state)
            StatsMediaPerPeriod(
                isLoading = state.isLoadingMediaByYear,
                mediaByPeriod = state.mediaByYear,
                title = strings.media_per_year,
                uniqueId = "year",
                bottomAxisLabel = strings.year,
            )
            StatsMediaPerPeriod(
                isLoading = state.isLoadingMediaByMonth,
                mediaByPeriod = state.mediaByMonth,
                title = strings.media_per_month,
                uniqueId = "month",
                bottomAxisLabel = strings.month,
            )
            StatsMediaPerPeriod(
                isLoading = state.isLoadingMediaByDayOfMonth,
                mediaByPeriod = state.mediaByDayOfMonth,
                title = strings.media_per_day_of_month,
                uniqueId = "day_of_month",
                bottomAxisLabel = strings.day,
            )
            StatsMediaPerPeriod(
                isLoading = state.isLoadingMediaByDayOfWeek,
                mediaByPeriod = state.mediaByDayOfWeek,
                title = strings.media_per_day_of_week,
                uniqueId = "day_of_week",
                bottomAxisLabel = strings.day,
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                StatsGroup(
                    isLoading = state.isLoadingMediaHeatMap,
                    title = strings.media_heatmap,
                    uniqueId = "heatmap",
                ) {
                    HeatMap(
                        properties = remember {
                            Properties()
                        },
                        records = remember(state.mediaHeatMap) {
                            state.mediaHeatMap.map { (day, count) ->
                                Record(
                                    date = LocalDate.of(day.year, day.month, day.day),
                                    value = count.toDouble(),
                                )
                            }
                        },
                        onSquareClick = {},
                    )
                }
            }
//            StatsTimeline(state.isLoadingTimeline, state.timeline)
            Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding() + 16.dp))
        }
    }
}

@Composable
private fun StatsHeader(state: StatsState) {
    Row {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(strings.photos),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = state.photoCount?.toString() ?: "-",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle2,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(strings.videos),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = state.videoCount?.toString() ?: "-",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle2,
            )
        }
    }
}

@Preview
@Composable
private fun StatsPreviewLoading() {
    PreviewAppTheme {
        Stats(state = StatsState())
    }
}

@Preview
@Composable
private fun StatsPreviewStats() {
    PreviewAppTheme {
        Stats(state = StatsState(
            isLoadingMediaByYear = false,
            mediaByYear = List(20) { it }.associateBy {
                Year(1978 + it)
            },
            isLoadingMediaByMonth = false,
            mediaByMonth = List(12) { it }.associateBy {
                Month(it)
            },
            isLoadingMediaByDayOfMonth = false,
            mediaByDayOfMonth = List(31) { it }.associateBy {
                DayOfMonth(it)
            },
            isLoadingMediaByDayOfWeek = false,
            mediaByDayOfWeek = List(7) { it }.associateBy {
                DayOfWeek(it)
            },
            isLoadingMediaHeatMap = false,
            mediaHeatMap = List(11) { it + 1 }.flatMap { month ->
                List(28) { it + 1 }.map { day ->
                    MediaDay(day, 0, month, 2023, "") to day
                }
            }.toMap(),
        ))
    }
}