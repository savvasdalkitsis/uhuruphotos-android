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
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.BlockedModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.FailedModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.IdleModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.InProgressModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatusModel.QueuedModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.cancel
import uhuruphotos_android.foundation.strings.api.generated.resources.start

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
            enabled = state.status !is BlockedModel,
            onClick = when (state.status) {
                IdleModel, BlockedModel, FailedModel -> onStartJob
                is InProgressModel, QueuedModel -> onCancelJob
            }
        ) {
            Text(
                modifier = Modifier.animateContentSize(),
                text = when (state.status) {
                    QueuedModel, BlockedModel, IdleModel, FailedModel -> stringResource(string.start)
                    is InProgressModel -> stringResource(string.cancel)
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
            AnimatedVisibility(visible = state.status is InProgressModel) {
                (state.status as? InProgressModel)?.let { status ->
                    Text("${status.progress}%")
                }
            }
        }
        AnimatedVisibility(visible = state.status is InProgressModel) {
            (state.status as? InProgressModel)?.let { status ->
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    strokeCap = StrokeCap.Round,
                    progress = { status.progress / 100f }
                )
            }
        }
    }
}

@Preview
@Composable
private fun JobInProgress() {
    Job(InProgressModel(progress = 25))
}

@Preview
@Composable
private fun JobIdle() {
    Job(IdleModel)
}

@Preview
@Composable
private fun JobBlocked() {
    Job(BlockedModel)
}

@Composable
private fun Job(status: JobStatusModel) {
    PreviewAppTheme {
        JobRow(
            JobState(
                title = Title.Text("Job title"),
                job = JobModel.FULL_FEED_SYNC,
                status
            )
        )
    }
}