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
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.R.raw
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.LogOut
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.NavigateToPrivacyPolicy
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.ShowLibrePhotosHelp
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.WelcomeAction
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.ui.state.WelcomeState
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui.PermissionsState
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button.UhuruIconOutlineButton
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
        ) {
            WelcomeUseCase(
                raw.animation_local_media,
                string.manage_media_on_device,
                state.localMediaSelected,
            ) {
                permissionState.askForPermissions()
            }
            WelcomeUseCase(
                if (state.localMediaSelected) raw.animation_cloud_backup else raw.animation_cloud,
                if (state.localMediaSelected) string.backup_media_on_cloud else string.manage_media_on_cloud,
                state.cloudMediaSelected,
            ) {
                action(ShowLibrePhotosHelp)
            }
        }
    }
}