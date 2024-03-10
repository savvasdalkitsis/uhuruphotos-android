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
package com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.R
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.seam.actions.Allow
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.seam.actions.NotificationsAction
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.seam.actions.Skip
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.seam.actions.ToggleRememberChoice
import com.savvasdalkitsis.uhuruphotos.feature.notifications.view.implementation.ui.state.NotificationsState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.layout.plus
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.DynamicIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ToggleableButtonWithIcon

@Composable
internal fun Notifications(
    state: NotificationsState,
    action: (NotificationsAction) -> Unit,
) {
    CommonScaffold(
        title = { Text(stringResource(string.notifications)) },
        navigationIcon = { Logo() },
    ) { contentPadding ->
        if (state.isLoading) {
            FullLoading()
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding + PaddingValues(8.dp))
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(string.notifications_explanation),
                    style = MaterialTheme.typography.h6,
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(string.notifications_explanation_subtext),
                    style = MaterialTheme.typography.subtitle2,
                )
                Spacer(modifier = Modifier.weight(1f))
                DynamicIcon(
                    modifier = Modifier.height(320.dp),
                    icon = R.raw.animation_notifications,
                )
                Spacer(modifier = Modifier.weight(1f))
                ToggleableButtonWithIcon(
                    icon = drawable.ic_remember,
                    text = stringResource(string.remember_choice),
                    checked = state.rememberChoice,
                ) {
                    action(ToggleRememberChoice)
                }
                Row(
                    horizontalArrangement = spacedBy(16.dp),
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { action(Skip) },
                    ) {
                        Text(stringResource(string.skip))
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { action(Allow) },
                    ) {
                        Text(stringResource(string.allow))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NotificationsPreview() {
    PreviewAppTheme {
        Notifications(state = NotificationsState(isLoading = false)) {}
    }
}