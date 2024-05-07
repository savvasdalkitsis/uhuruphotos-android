/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Blocked
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Failed
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Idle
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.InProgress
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Queued
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun JobRow(
    state: JobState,
    onStartJob: () -> Unit = {},
    onCancelJob: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .align(CenterVertically)
        ) {
            JobProgressIndicator(state)
        }
        Button(
            modifier = Modifier
                .padding(8.dp)
                .align(CenterVertically),
            enabled = state.status !is Blocked,
            onClick = when (state.status) {
                Idle, Blocked, Failed -> onStartJob
                is InProgress, Queued -> onCancelJob
            }
        ) {
            Text(
                modifier = Modifier.animateContentSize(),
                text = when (state.status) {
                    Queued, Blocked, Idle, Failed -> stringResource(strings.start)
                    is InProgress -> stringResource(strings.cancel)
                }
            )
        }
    }
}

@Composable
private fun JobProgressIndicator(
    state: JobState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(modifier = Modifier.weight(1f), text = state.title.toText())
            AnimatedVisibility(visible = state.status is InProgress) {
                (state.status as? InProgress)?.let { status ->
                    Text("${status.progress}%")
                }
            }
        }
        AnimatedVisibility(visible = state.status is InProgress) {
            (state.status as? InProgress)?.let { status ->
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    strokeCap = StrokeCap.Round,
                    progress = status.progress / 100f
                )
            }
        }
    }
}