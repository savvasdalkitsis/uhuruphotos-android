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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
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
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeVariant
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.layout.plus
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.ToggleableButtonWithIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold

@Composable
internal fun Notifications(
    state: NotificationsState,
    action: (NotificationsAction) -> Unit,
) {
    UhuruScaffold(
        title = { Text(stringResource(string.notifications)) },
        navigationIcon = { Logo() },
    ) { contentPadding ->
        if (state.isLoading) {
            UhuruFullLoading()
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding + PaddingValues(8.dp))
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(string.notifications_explanation),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(string.notifications_explanation_subtext),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.weight(1f))
                UhuruIcon(
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

private data class PreviewData(
    val themeMode: ThemeMode,
    val themeVariant: ThemeVariant,
)

private class PreviewDataProvider : PreviewParameterProvider<PreviewData> {
    override val values: Sequence<PreviewData> =
        ThemeMode.entries.flatMap { themeMode ->
            ThemeVariant.entries.flatMap { themeVariant ->
                listOf(
                    PreviewData(themeMode, themeVariant),
                    PreviewData(themeMode, themeVariant),
                    PreviewData(themeMode, themeVariant),
                    PreviewData(themeMode, themeVariant),
                )
            }
        }.asSequence()
}

@Preview
@Composable
private fun UhuruIconPreview(@PreviewParameter(PreviewDataProvider::class) data: PreviewData) {
    PreviewAppTheme(
        themeMode = data.themeMode,
        theme = data.themeVariant,
    ) {

        Notifications(state = NotificationsState(isLoading = false)) {}
    }
}