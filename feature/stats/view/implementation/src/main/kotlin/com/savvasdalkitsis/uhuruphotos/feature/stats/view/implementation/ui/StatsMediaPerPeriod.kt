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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.Period
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.NoContent

@Composable
internal fun StatsMediaPerPeriod(
    isLoading: Boolean,
    mediaByPeriod: Map<out Period, Int>,
    title: Int,
    uniqueId: String,
    bottomAxisLabel: Int,
) {
    val model = remember(mediaByPeriod) {
        ChartEntryModelProducer(mediaByPeriod.map { FloatEntry(it.key.value.toFloat(), it.value.toFloat()) })
    }
    StatsGroup(title, uniqueId, isLoading) {
        if (mediaByPeriod.isEmpty()) {
            NoContent(
                message = string.no_media,
                refreshable = false,
            )
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
                        guideline = lineComponent(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)),
                    ),
                    startAxis = rememberStartAxis(
                        label = axisLabelComponent(verticalPadding = 8.dp),
                        valueFormatter = { v, _ -> v.toInt().toString() }
                    ),
                    bottomAxis = rememberBottomAxis(
                        title = stringResource(bottomAxisLabel),
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