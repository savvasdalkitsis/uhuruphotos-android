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
package com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.portfolio.view.implementation.R
import com.savvasdalkitsis.uhuruphotos.foundation.permissions.api.ui.PermissionsState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewThemeData
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewThemeDataProvider
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.icon.UhuruIcon

@Composable
fun BoxScope.PortfolioMissingPermissions(
    missingPermissions: List<String>? = null,
) {
    val permissionsState by PermissionsState.rememberPermissionsState(missingPermissions)
    Column(
        modifier = Modifier
            .padding(4.dp)
            .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(8.dp),
    ) {
        UhuruIcon(icon = R.raw.animation_media_file)
        Text(
            text = stringResource(string.missing_permissions),
            style = MaterialTheme.typography.headlineMedium,
        )
        Button(onClick = { permissionsState.askForPermissions() }) {
            Text(text = stringResource(string.grant_permissions))
        }
    }
}

@Preview
@Composable
private fun UhuruIconPreview(@PreviewParameter(PreviewThemeDataProvider::class) data: PreviewThemeData) {
    PreviewAppTheme(data) {
        Box {
            PortfolioMissingPermissions()
        }
    }
}