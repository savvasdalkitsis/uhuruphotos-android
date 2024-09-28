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

import androidx.compose.runtime.Composable
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobModel
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import kotlinx.collections.immutable.persistentListOf

@Composable
fun Jobs(
    jobs: List<JobState>,
    blockFilter: List<JobModel> = persistentListOf(),
    onStartJob: (JobModel) -> Unit,
    onCancelJob: (JobModel) -> Unit,
) {
    jobs
        .sortedBy { it.job.order }
        .filter { it.job !in blockFilter }
        .forEach { state ->
            JobRow(
                state = state,
                onStartJob = { onStartJob(state.job) },
                onCancelJob = { onCancelJob(state.job) }
            )
        }
}