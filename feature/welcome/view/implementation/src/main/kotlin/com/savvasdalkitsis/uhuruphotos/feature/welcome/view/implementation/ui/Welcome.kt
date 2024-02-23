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

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components.WelcomeNeedsAccessDialog
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components.WelcomePermissionRationale
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui.PermissionsState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.ActionIcon
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.CommonScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import dev.shreyaspatil.permissionflow.compose.rememberPermissionFlowRequestLauncher

@Composable
internal fun Welcome(
    state: WelcomeState,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>? = rememberPermissionFlowRequestLauncher(),
    action: (WelcomeAction) -> Unit,
) {
    CommonScaffold(
        title = { Logo() },
        actionBarContent = {
            ActionIcon(
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
                state.isLoading -> FullLoading(modifier = Modifier.weight(1f))
                else -> {
                    Spacer(modifier = Modifier.weight(1f))
                    WelcomeLoadedContent(state, action, permissionState)
                }
            }
            Text(
                text = stringResource(string.welcome_footnote),
                style = MaterialTheme.typography.caption,
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSaveEnabled,
                onClick = { action(Save) }
            ) {
                Text(text = stringResource(string.save))
            }
        }
        if (state.showPermissionRationale) {
            WelcomePermissionRationale(action, state, permissionLauncher)
        }
        if (state.showNeedsAccess) {
            WelcomeNeedsAccessDialog(action, state, permissionLauncher)
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
        WelcomeState(isLoading = false, showPermissionRationale = true),
        WelcomeState(isLoading = false, showNeedsAccess = true),
        WelcomeState(isLoading = false, showLibrePhotosHelp = true),
    )
}

@Preview
@Composable
private fun WelcomePreview(@PreviewParameter(WelcomePreviews::class) state: WelcomeState) {
    PreviewAppTheme {
        Welcome(
            state = state,
            permissionLauncher = null,
        ) {}
    }
}
@Preview
@Composable
private fun WelcomePreviewDark(@PreviewParameter(WelcomePreviews::class) state: WelcomeState) {
    PreviewAppTheme(darkTheme = true) {
        Welcome(
            state = state,
            permissionLauncher = null,
        ) {}
    }
}