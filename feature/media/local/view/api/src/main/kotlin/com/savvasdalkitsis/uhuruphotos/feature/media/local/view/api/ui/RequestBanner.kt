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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.view.api.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.sharedelement.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.text.AlertText
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import uhuruphotos_android.foundation.strings.api.generated.resources.Res.string
import uhuruphotos_android.foundation.strings.api.generated.resources.do_not_ask_again
import uhuruphotos_android.foundation.strings.api.generated.resources.grant_permissions

@Composable
fun RequestBanner(
    modifier: Modifier = Modifier,
    description: StringResource,
    warning: StringResource? = null,
    grantText: StringResource = string.grant_permissions,
    onAccessGranted: () -> Unit,
    onNeverRemindMeAgain: () -> Unit,
) {
    RequestBanner(
        modifier = modifier,
        description = description,
        warning = warning,
        grantButton = {
            Button(
                modifier = Modifier
                    .recomposeHighlighter()
                    .fillMaxWidth()
                    .weight(1f),
                onClick = onAccessGranted,
            ) {
                Text(stringResource(grantText))
            }
        },
        denyButton = {
            OutlinedButton(
                modifier = Modifier
                    .recomposeHighlighter()
                    .fillMaxWidth()
                    .weight(1f),
                onClick = onNeverRemindMeAgain
            ) {
                Text(stringResource(string.do_not_ask_again))
            }
        },
    )
}



@Composable
fun RequestBanner(
    modifier: Modifier = Modifier,
    description: StringResource,
    warning: StringResource? = null,
    grantButton: @Composable (RowScope.() -> Unit),
    denyButton: @Composable (RowScope.() -> Unit),
){
    Card(
        modifier = modifier
            .recomposeHighlighter()
        ,
    ) {
        Column(
            modifier = Modifier
                .recomposeHighlighter()
                .fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier
                    .recomposeHighlighter()
                    .fillMaxWidth()
                    .padding(8.dp),
                text = stringResource(description),
                textAlign = TextAlign.Center,
            )
            if (warning != null) {
                AlertText(text = stringResource(warning))
            }
            Row(
                modifier = Modifier
                    .recomposeHighlighter()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                denyButton()
                grantButton()
            }
        }
    }
}