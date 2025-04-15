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
package com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.ChangeThemeMode
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.ChangeThemeVariant
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.CollageShapeChanged
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.CollageSpacingChanged
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.CollageSpacingEdgeChanged
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.Continue
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.seam.actions.ThemeAction
import com.savvasdalkitsis.uhuruphotos.feature.theme.view.implementation.ui.state.ThemeState
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.PreviewAppTheme
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeMode
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.Logo
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.UhuruScaffold
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.theme.UhuruThemeSettings
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.choose_app_theme
import uhuruphotos_android.foundation.strings.api.generated.resources.continue_

@Composable
internal fun Theme(
    state: ThemeState,
    action: (ThemeAction) -> Unit,
) {
    UhuruScaffold(
        title = { Text(stringResource(string.choose_app_theme)) },
        navigationIcon = { Logo() },
        bottomBarDisplayed = true,
        bottomBarContent = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { action(Continue) },
                ) {
                    Text(stringResource(string.continue_))
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()
            ),
        ) {
            UhuruThemeSettings(
                state = state.settings,
                onCollageSpacingChanged = { action(CollageSpacingChanged(it)) },
                onCollageSpacingEdgeChanged = { action(CollageSpacingEdgeChanged(it)) },
                onChangeThemeVariant = { v, c -> action(ChangeThemeVariant(v, c)) },
                onChangeThemeMode = { action(ChangeThemeMode(it)) },
                onCollageShapeChanged = { action(CollageShapeChanged(it)) },
            )
        }
    }
}

@Preview
@Composable
fun ThemePreview() {
    PreviewAppTheme {
        Theme(
            state = ThemeState(),
            action = {}
        )
    }
}

@Preview
@Composable
fun ThemePreviewDark() {
    PreviewAppTheme(themeMode = ThemeMode.DARK_MODE) {
        Theme(
            state = ThemeState(),
            action = {}
        )
    }
}