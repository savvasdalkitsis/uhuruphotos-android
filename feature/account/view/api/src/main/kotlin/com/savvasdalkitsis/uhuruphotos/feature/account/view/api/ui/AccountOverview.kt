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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import com.savvasdalkitsis.uhuruphotos.feature.account.view.api.ui.state.AccountOverviewState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.Avatar
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.AvatarState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.SyncState
import com.savvasdalkitsis.uhuruphotos.feature.avatar.view.api.ui.state.previewAvatarState
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.FEED_DETAILS_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.Job.FULL_FEED_SYNC
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Blocked
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Idle
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.InProgress
import com.savvasdalkitsis.uhuruphotos.feature.jobs.domain.api.model.JobStatus.Queued
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.Jobs
import com.savvasdalkitsis.uhuruphotos.feature.jobs.view.ui.state.JobState
import com.savvasdalkitsis.uhuruphotos.feature.uploads.view.api.ui.UploadsRow
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.raw
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.ToggleableButtonWithIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.CollapsibleGroup
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.group.state.rememberCollapsibleGroupState
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.state.Title
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun AccountOverview(
    modifier: Modifier = Modifier,
    state: AccountOverviewState,
    onAboutClicked: () -> Unit = {},
    onFoldersClicked: () -> Unit = {},
    onLogoutClicked: () -> Unit = {},
    onLoginClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onStartJob: (Job) -> Unit = {},
    onCancelJob: (Job) -> Unit = {},
    onClose: () -> Unit = {},
    onViewAllUploadsClicked: () -> Unit = {},
    onCloudSyncChanged: (Boolean) -> Unit = {},
) {
    ConstraintLayout(
        modifier = modifier
            .recomposeHighlighter()
            .defaultMinSize(minHeight = 120.dp)
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        val (headerRef, contentRef, footerRef) = createRefs()
        Header(headerRef, state, onClose)
        Footer(contentRef, footerRef, state, onLoginClicked, onLogoutClicked, onSettingsClicked)
        Content(headerRef,
            contentRef,
            footerRef,
            state,
            onStartJob,
            onCancelJob,
            onViewAllUploadsClicked,
            onCloudSyncChanged,
            onAboutClicked,
            onFoldersClicked,
        )
    }
}

@Composable
private fun ConstraintLayoutScope.Header(
    headerRef: ConstrainedLayoutReference,
    state: AccountOverviewState,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .constrainAs(headerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
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
}

@Composable
private fun ConstraintLayoutScope.Content(
    headerRef: ConstrainedLayoutReference,
    contentRef: ConstrainedLayoutReference,
    footerRef: ConstrainedLayoutReference,
    state: AccountOverviewState,
    onStartJob: (Job) -> Unit,
    onCancelJob: (Job) -> Unit,
    onViewAllUploadsClicked: () -> Unit,
    onCloudSyncChanged: (Boolean) -> Unit,
    onAboutClicked: () -> Unit,
    onFoldersClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .constrainAs(contentRef) {
                top.linkTo(headerRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(footerRef.top)
            }
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
        ) {
            CollapsibleGroup(
                modifier = Modifier,
                groupState = rememberCollapsibleGroupState(
                    title = string.jobs,
                    uniqueKey = "accountOverviewJobs",
                )
            ) {
                Jobs(
                    jobs = state.jobs,
                    blockFilter = remember { persistentListOf(FULL_FEED_SYNC, FEED_DETAILS_SYNC) },
                    onStartJob = onStartJob,
                    onCancelJob = onCancelJob,
                )
                if (state.showUploads) {
                    UploadsRow(inProgress = state.uploadsInProgress, onViewAllUploadsClicked)
                }
            }
        }
        AnimatedVisibility(visible = state.showCloudSync) {
            ToggleableButtonWithIcon(
                modifier = Modifier
                    .recomposeHighlighter()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                iconModifier = Modifier.size(48.dp),
                icon = if (MaterialTheme.colors.isLight)
                    raw.animation_syncing
                else
                    raw.animation_syncing_dark,
                animateIfAvailable = state.cloudBackUpEnabled,
                text = stringResource(string.cloud_sync),
                checked = state.cloudBackUpEnabled,
                onCheckedChange = onCloudSyncChanged,
            )
        }
        Row(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f)
                    .fillMaxWidth(),
                onClick = onFoldersClicked,
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_folder),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(string.feed_folders))
            }
            OutlinedButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .weight(1f)
                    .fillMaxWidth(),
                onClick = onAboutClicked,
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_info),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(string.about))
            }
        }
    }
}

@Composable
private fun ConstraintLayoutScope.Footer(
    contentRef: ConstrainedLayoutReference,
    footerRef: ConstrainedLayoutReference,
    state: AccountOverviewState,
    onLoginClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .recomposeHighlighter()
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .constrainAs(footerRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                top.linkTo(contentRef.bottom)
            },
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
                painter = painterResource(
                    id = if (state.showLogIn)
                        drawable.ic_login
                    else
                        drawable.ic_logout
                ),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (state.showLogIn)
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

@Preview
@Composable
private fun AccountOverviewPreview() {
    PreviewAppTheme {
        AccountOverview(
            modifier = Modifier.fillMaxWidth(),
            state = AccountOverviewState(
                showLogIn = false,
                showUserAndServerDetails = true,
                showUploads = true,
                showCloudSync = true,
                avatarState = previewAvatarState,
                jobs = persistentListOf(
                    JobState(Title.Text("Feed"), FULL_FEED_SYNC, Idle),
                    JobState(Title.Text("Precache"), FULL_FEED_SYNC, Blocked),
                    JobState(Title.Text("Local"), FULL_FEED_SYNC, InProgress(25)),
                    JobState(Title.Text("Queued"), FULL_FEED_SYNC, Queued),
                )
            ),
        )
    }
}

@Preview
@Composable
private fun AccountOverviewDarkPreview() {
    PreviewAppTheme(theme = ThemeMode.DARK_MODE) {
        AccountOverview(
            modifier = Modifier.fillMaxWidth(),
            state = AccountOverviewState(
                showLogIn = false,
                showUserAndServerDetails = true,
                showUploads = true,
                showCloudSync = true,
                avatarState = previewAvatarState,
                jobs = persistentListOf(
                    JobState(Title.Text("Feed"), FULL_FEED_SYNC, Idle),
                    JobState(Title.Text("Precache"), FULL_FEED_SYNC, Blocked),
                    JobState(Title.Text("Local"), FULL_FEED_SYNC, InProgress(25)),
                    JobState(Title.Text("Queued"), FULL_FEED_SYNC, Queued),
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
                    JobState(Title.Text("Local"), FULL_FEED_SYNC, InProgress(25)),
                )
            ),
        )
    }
}

@Preview
@Composable
private fun AccountOverviewPreviewSmall() {
    PreviewAppTheme {
        Box(modifier = Modifier.height(320.dp)) {
            AccountOverview(
                modifier = Modifier.fillMaxWidth(),
                state = AccountOverviewState(
                    showLogIn = false,
                    showUserAndServerDetails = true,
                    showUploads = true,
                    showCloudSync = true,
                    avatarState = previewAvatarState,
                    jobs = persistentListOf(
                        JobState(Title.Text("Feed"), FULL_FEED_SYNC, Idle),
                        JobState(Title.Text("Precache"), FULL_FEED_SYNC, Blocked),
                        JobState(Title.Text("Local"), FULL_FEED_SYNC, InProgress(25)),
                        JobState(Title.Text("Queued"), FULL_FEED_SYNC, Queued),
                    )
                ),
            )
        }
    }
}