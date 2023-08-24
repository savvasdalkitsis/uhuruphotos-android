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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.Avatar
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.previewAvatarState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Blocked
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Idle
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.InProgress
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Queued
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.Jobs
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.theme.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.rememberCollapsibleGroupState
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AccountOverview(
    modifier: Modifier = Modifier,
    state: AccountOverviewState,
    onAboutClicked: () -> Unit = {},
    onLogoutClicked: () -> Unit = {},
    onLoginClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onStartJob: (Job) -> Unit = {},
    onCancelJob: (Job) -> Unit = {},
    onClose: () -> Unit = {},
    onViewAllUploadsClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .recomposeHighlighter()
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(
                state = state.avatarState,
                size = 48.dp
            )
            if (state.showUserAndServerDetails) {
                Column(
                    modifier = Modifier
                        .recomposeHighlighter()
                        .weight(1f)
                ) {
                    Text(
                        text = state.avatarState.userFullName,
                        style = TextStyle.Default.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = state.avatarState.serverUrl,
                        style = MaterialTheme.typography.caption.copy(color = Color.Gray),
                    )
                }
            } else {
                Text(
                    modifier = Modifier
                        .recomposeHighlighter()
                        .weight(1f),
                    text = "UhuruPhotos",
                    style = MaterialTheme.typography.h5,
                )
            }
            ActionIcon(
                onClick = onClose,
                icon = drawable.ic_close,
                contentDescription = stringResource(string.close)
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp),
        ) {
            CollapsibleGroup(
                groupState = rememberCollapsibleGroupState(
                    title = string.jobs,
                    uniqueKey = "accountOverviewJobs",
                )
            ) {
                Jobs(
                    jobs = state.jobs,
                    onStartJob = onStartJob,
                    onCancelJob = onCancelJob,
                )
                if (state.showUploads) {
                    Uploads(inProgress = state.uploadsInProgress, onViewAllUploadsClicked)
                }
            }
        }
        OutlinedButton(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = onAboutClicked,
        ) {
            Icon(
                painter = painterResource(id = drawable.ic_info),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(string.about))
        }
        Row(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = spacedBy(8.dp),
        ) {
            OutlinedButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f),
                onClick = if (state.showLogIn)
                    onLoginClicked
                else
                    onLogoutClicked,
            ) {
                Icon(
                    painter = painterResource(id = if (state.showLogIn)
                        drawable.ic_login
                    else
                        drawable.ic_logout
                    ),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (state.showLogIn)
                    stringResource(string.login)
                else
                    stringResource(string.log_out)
                )
            }
            OutlinedButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f),
                onClick = onSettingsClicked
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(string.settings))
            }
        }
    }
}

@Composable
private fun Uploads(
    inProgress: Boolean,
    onViewAll: () -> Unit,
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
                .align(Alignment.CenterVertically)
        ) {
            UploadsProgress(inProgress)
        }
        Button(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            onClick = onViewAll,
        ) {
            Text(
                modifier = Modifier.animateContentSize(),
                text = stringResource(string.view_all),
            )
        }
    }
}

@Composable
private fun UploadsProgress(inProgress: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(modifier = Modifier
            .fillMaxWidth(),
            text = stringResource(string.uploads)
        )
        AnimatedVisibility(visible = inProgress) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                strokeCap = StrokeCap.Round,
            )
        }
    }
}

@Preview
@Composable
private fun AccountOverviewPreview() {
    PreviewAppTheme {
        AccountOverview(
            modifier = Modifier.fillMaxWidth(),
            state = AccountOverviewState(
                showLogIn = false,
                showUserAndServerDetails = true,
                avatarState = previewAvatarState,
                jobs = persistentListOf(
                    JobState(Title.Text("Feed"), Job.FEED_SYNC, Idle),
                    JobState(Title.Text("Precache"), Job.FEED_SYNC, Blocked),
                    JobState(Title.Text("Local"), Job.FEED_SYNC, InProgress(25)),
                    JobState(Title.Text("Queued"), Job.FEED_SYNC, Queued),
                )
            ),
        )
    }
}

@Preview
@Composable
private fun AccountOverviewPreviewLoggedOut() {
    PreviewAppTheme {
        AccountOverview(
            modifier = Modifier.fillMaxWidth(),
            state = AccountOverviewState(
                showLogIn = true,
                showUserAndServerDetails = false,
                avatarState = AvatarState(syncState = SyncState.GOOD),
                jobs = persistentListOf(
                    JobState(Title.Text("Local"), Job.FEED_SYNC, InProgress(25)),
                )
            ),
        )
    }
}