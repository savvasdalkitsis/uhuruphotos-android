/*
Copyright 2022 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.settings.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingsOutlineButtonRow(
    enabled: Boolean = true,
    buttonText: String,
    @DrawableRes icon: Int? = null,
    onClick: (() -> Unit),
) {
    Box(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth(),
    ) {
        OutlinedButton(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            enabled = enabled,
            onClick = onClick
        ) {
            icon?.let {
                Icon(painter = painterResource(id = it), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(buttonText)
        }
    }
}