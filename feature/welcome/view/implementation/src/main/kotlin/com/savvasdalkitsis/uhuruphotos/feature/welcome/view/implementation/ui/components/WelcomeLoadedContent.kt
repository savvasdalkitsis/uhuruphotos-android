/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.LogOut
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.NavigateToPrivacyPolicy
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.ShowLibrePhotosHelp
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.WelcomeAction
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.animation.AnimationResource
import com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui.PermissionsState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.UhuruIconOutlineButton
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_book_open
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.backup_media_on_cloud
import uhuruphotos_android.foundation.strings.api.generated.resources.log_out
import uhuruphotos_android.foundation.strings.api.generated.resources.manage_media_on_cloud
import uhuruphotos_android.foundation.strings.api.generated.resources.manage_media_on_device
import uhuruphotos_android.foundation.strings.api.generated.resources.privacy_policy

@Composable
internal fun WelcomeLoadedContent(
    state: WelcomeState,
    action: (WelcomeAction) -> Unit,
    permissionState: PermissionsState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            UhuruIconOutlineButton(
                modifier = Modifier.weight(1f),
                icon = drawable.ic_book_open,
                onClick = { action(NavigateToPrivacyPolicy) },
                text = stringResource(string.privacy_policy)
            )
            if (state.cloudMediaSelected) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { action(LogOut) }
                ) {
                    Text(stringResource(string.log_out))
                }
            }
        }
        Row(
            modifier = Modifier.sizeIn(maxHeight = 240.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WelcomeUseCase(
                AnimationResource.animation_local_media,
                string.manage_media_on_device,
                state.localMediaSelected,
            ) {
                permissionState.askForPermissions()
            }
            WelcomeUseCase(
                if (state.localMediaSelected) AnimationResource.animation_cloud_backup else AnimationResource.animation_cloud,
                if (state.localMediaSelected) string.backup_media_on_cloud else string.manage_media_on_cloud,
                state.cloudMediaSelected,
            ) {
                action(ShowLibrePhotosHelp)
            }
        }
    }
}

@Preview
@Composable
private fun WelcomeLoadedContentPreview() {
    PreviewAppTheme {
        WelcomeLoadedContent(
            state = WelcomeState(
                isLoading = false,
                localMediaSelected = true,
                cloudMediaSelected = true,
            ),
            action = {},
            permissionState = PermissionsState()
        )
    }
}
