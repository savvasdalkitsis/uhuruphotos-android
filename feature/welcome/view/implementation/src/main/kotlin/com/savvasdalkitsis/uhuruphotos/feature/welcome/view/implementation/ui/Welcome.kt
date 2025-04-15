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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.Help
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.Save
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.WelcomeAction
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components.WelcomeHeader
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components.WelcomeLibrePhotosHelpDialog
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components.WelcomeLoadedContent
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui.PermissionsState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.UhuruFullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.save
import uhuruphotos_android.foundation.strings.api.generated.resources.welcome_footnote

@Composable
internal fun Welcome(
    state: WelcomeState,
    action: (WelcomeAction) -> Unit,
) {
    UhuruScaffold(
        title = { Logo() },
        actionBarContent = {
            UhuruActionIcon(
                onClick = { action(Help) },
                icon = drawable.ic_help,
            )
        }
    ) { contentPadding ->
        val permissionState by PermissionsState.rememberPermissionsState(state.missingPermissions)
        Column(
            modifier = Modifier
                .padding(
                    top = contentPadding.calculateTopPadding(),
                    start = 24.dp,
                    end = 24.dp,
                    bottom = contentPadding.calculateBottomPadding() + 24.dp,
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = spacedBy(12.dp)
        ) {
            WelcomeHeader()
            when {
                state.isLoading -> UhuruFullLoading(modifier = Modifier.weight(1f))
                else -> {
                    Spacer(modifier = Modifier.weight(1f))
                    WelcomeLoadedContent(state, action, permissionState)
                }
            }
            Text(
                text = stringResource(string.welcome_footnote),
                style = MaterialTheme.typography.bodySmall,
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSaveEnabled,
                onClick = { action(Save) }
            ) {
                Text(text = stringResource(string.save))
            }
        }
        if (state.showLibrePhotosHelp) {
            WelcomeLibrePhotosHelpDialog(action)
        }
    }
}

private class WelcomePreviews : PreviewParameterProvider<WelcomeState> {
    override val values: Sequence<WelcomeState> = sequenceOf(
        WelcomeState(isLoading = true),
        WelcomeState(isLoading = false),
        WelcomeState(isLoading = false, localMediaSelected = true, isSaveEnabled = true),
        WelcomeState(isLoading = false, cloudMediaSelected = true, isSaveEnabled = true),
        WelcomeState(isLoading = false, cloudMediaSelected = true, localMediaSelected = true, isSaveEnabled = true),
        WelcomeState(isLoading = false, showLibrePhotosHelp = true),
    )
}

@Preview
@Composable
private fun WelcomePreview(@PreviewParameter(WelcomePreviews::class) state: WelcomeState) {
    PreviewAppTheme {
        Welcome(
            state = state,
        ) {}
    }
}
@Preview
@Composable
private fun WelcomePreviewDark(@PreviewParameter(WelcomePreviews::class) state: WelcomeState) {
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        Welcome(
            state = state,
        ) {}
    }
}