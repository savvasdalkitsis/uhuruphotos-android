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
package com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.Avatar
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.previewAvatarState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Blocked
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Idle
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.InProgress
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Queued
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.Jobs
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title

@Composable
internal fun AccountOverview(
    modifier: Modifier = Modifier,
    state: AccountOverviewState,
    onLogoutClicked: () -> Unit = {},
    onEditServerClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onStartJob: (Job) -> Unit = {},
    onCancelJob: (Job) -> Unit = {},
    onClose: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(
                state = state.avatarState,
                size = 48.dp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.avatarState.userFullName,
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = state.avatarState.serverUrl,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                )
            }
            ActionIcon(
                onClick = onEditServerClicked,
                icon = drawable.ic_edit,
                contentDescription = stringResource(string.edit_server_url)
            )
            ActionIcon(
                onClick = onClose,
                icon = drawable.ic_close,
                contentDescription = stringResource(string.close)
            )
        }
        Column {
            SectionHeader(title = stringResource(string.jobs))
            Jobs(
                jobs = state.jobs,
                onStartJob = onStartJob,
                onCancelJob = onCancelJob,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onLogoutClicked,
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_logout),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(string.log_out))
            }
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onSettingsClicked
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(string.settings))
            }
        }
    }
}

@Preview
@Composable
fun AccountOverviewPreview() {
    PreviewAppTheme {
        AccountOverview(
            modifier = Modifier.fillMaxWidth(),
            state = AccountOverviewState(
                avatarState = previewAvatarState,
                jobs = listOf(
                    JobState(Title.Text("Feed"), Job.FEED_SYNC, Idle),
                    JobState(Title.Text("Precache"), Job.FEED_SYNC, Blocked),
                    JobState(Title.Text("Local"), Job.FEED_SYNC, InProgress(25)),
                    JobState(Title.Text("Queued"), Job.FEED_SYNC, Queued),
                )
            ),
        )
    }
}