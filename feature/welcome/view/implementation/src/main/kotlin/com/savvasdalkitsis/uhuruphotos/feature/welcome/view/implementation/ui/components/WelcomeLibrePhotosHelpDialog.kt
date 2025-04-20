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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.HideLibrePhotosHelp
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.SelectCloudMedia
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.TakeUserToLibrePhotosWebsite
import com.savvasdalkitsis.uhuruphotos.feature.welcome.view.implementation.seam.actions.WelcomeAction
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.dialogs.YesNoDialog
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_help
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.enable_cloud_sync
import uhuruphotos_android.foundation.strings.api.generated.resources.libre_photos_description
import uhuruphotos_android.foundation.strings.api.generated.resources.need_a_libre_photos_server_1
import uhuruphotos_android.foundation.strings.api.generated.resources.need_a_libre_photos_server_2
import uhuruphotos_android.foundation.strings.api.generated.resources.take_me_to_website
import uhuruphotos_android.foundation.strings.api.generated.resources.what_is_libre_photos

@Composable
internal fun WelcomeLibrePhotosHelpDialog(action: (WelcomeAction) -> Unit) {
    YesNoDialog(
        title = stringResource(string.enable_cloud_sync),
        onYes = {
            action(SelectCloudMedia)
        },
        onDismiss = { action(HideLibrePhotosHelp) },
    ) {
        Text(stringResource(string.need_a_libre_photos_server_1))
        Text(
            stringResource(string.what_is_libre_photos),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
        Text(stringResource(string.libre_photos_description))
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { action(TakeUserToLibrePhotosWebsite) }
        ) {
            Icon(
                painter = painterResource(drawable.ic_help),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(string.take_me_to_website))
        }
        Text(
            stringResource(string.need_a_libre_photos_server_2),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Preview
@Composable
private fun WelcomeLibrePhotosHelpDialogPreview() {
    PreviewAppTheme {
        WelcomeLibrePhotosHelpDialog(
            action = {},
        )
    }
}

