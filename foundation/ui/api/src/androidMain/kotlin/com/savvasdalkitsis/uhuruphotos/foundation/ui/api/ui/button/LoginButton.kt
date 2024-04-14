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
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.button

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.compose.api.recomposeHighlighter
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.R

@Composable
fun LoginButton(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .recomposeHighlighter(),
    onLogin: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onLogin,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_login),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string.login))
    }
}