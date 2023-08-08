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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.marker.markerComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.m2.style.m2ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Year
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.rememberCollapsibleGroupState

@Composable
internal fun StatsMediaPerYear(isLoading: Boolean, mediaByYear: Map<Year, Int>) {
    val model = remember(mediaByYear) {
        ChartEntryModelProducer(mediaByYear.map { FloatEntry(it.key.year.toFloat(), it.value.toFloat()) })
    }
    val groupState = rememberCollapsibleGroupState(R.string.media_per_year, "stats_media_per_year")
    CollapsibleGroup(groupState = groupState) {
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 240.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else {
            ProvideChartStyle(m2ChartStyle()) {
                Chart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 240.dp),
                    chart = columnChart(
                        spacing = 4.dp,
                    ),
                    chartModelProducer = model,
                    chartScrollSpec = rememberChartScrollSpec(
                        initialScroll = InitialScroll.End
                    ),
                    marker = markerComponent(
                        label = textComponent(),
                        indicator = shapeComponent(),
                        guideline = lineComponent(color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)),
                    ),
                    startAxis = rememberStartAxis(
                        label = axisLabelComponent(verticalPadding = 8.dp),
                        valueFormatter = { v, _ -> v.toInt().toString() }
                    ),
                    bottomAxis = rememberBottomAxis(
                        title = stringResource(R.string.year),
                        titleComponent = axisLabelComponent(),
                        valueFormatter = { v, _ -> v.toInt().toString() },
                        labelRotationDegrees = 90f,
                        label = axisLabelComponent(
                            textSize = 8.sp,
                        ),
                    ),
                    isZoomEnabled = true,
                    getXStep = { 1f },
                    runInitialAnimation = true,
                )
            }
        }
    }
}