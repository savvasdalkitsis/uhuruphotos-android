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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.stats.domain.api.model.CountryVisit
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.UhuruCollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.state.rememberCollapsibleGroupState
import com.yeocak.timelineview.TimelineView
import com.yeocak.timelineview.TimelineView.NodeType.FIRST
import com.yeocak.timelineview.TimelineView.NodeType.LAST
import com.yeocak.timelineview.TimelineView.NodeType.MIDDLE
import kotlinx.collections.immutable.ImmutableList

@Composable
fun StatsTimeline(isLoading: Boolean, timeline: ImmutableList<CountryVisit>) {
    val groupState = rememberCollapsibleGroupState(R.string.timeline, "stats_timeline")
    UhuruCollapsibleGroup(groupState = groupState) {
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 480.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 480.dp),
            ) {
                for ((index, visit) in timeline.withIndex()) {
                    item(index) {
                        Row {
                            TimelineView.SingleNode(
                                color = MaterialTheme.colorScheme.onBackground,
                                nodeType = when (index) {
                                    0 -> FIRST
                                    timeline.size - 1 -> LAST
                                    else -> MIDDLE
                                },
                                nodeSize = 48f,
                            )
                            Column {
                                Text(visit.dateRangeDisplayText)
                                Text(visit.country)
                            }
                        }
                    }
                }
            }
        }
    }
}