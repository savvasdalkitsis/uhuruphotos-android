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

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.stats.view.implementation.ui.state.StatsState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UpNavButton

@Composable
internal fun Stats(
    state: StatsState,
) {
    CommonScaffold(
        title = { Text(stringResource(string.stats)) },
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
            StatsMediaPerPeriod(
                isLoading = state.isLoadingMediaByYear,
                mediaByPeriod = state.mediaByYear,
                title = string.media_per_year,
                uniqueId = "year",
                bottomAxisLabel = string.year,
            )
            StatsMediaPerPeriod(
                isLoading = state.isLoadingMediaByMonth,
                mediaByPeriod = state.mediaByMonth,
                title = string.media_per_month,
                uniqueId = "month",
                bottomAxisLabel = string.month,
            )
            StatsMediaPerPeriod(
                isLoading = state.isLoadingMediaByDayOfMonth,
                mediaByPeriod = state.mediaByDayOfMonth,
                title = string.media_per_day_of_month,
                uniqueId = "day_of_month",
                bottomAxisLabel = string.day,
            )
            StatsMediaPerPeriod(
                isLoading = state.isLoadingMediaByDayOfWeek,
                mediaByPeriod = state.mediaByDayOfWeek,
                title = string.media_per_day_of_week,
                uniqueId = "day_of_week",
                bottomAxisLabel = string.day,
            )
//            StatsTimeline(state.isLoadingTimeline, state.timeline)
            Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding() + 16.dp))
        }
    }
}