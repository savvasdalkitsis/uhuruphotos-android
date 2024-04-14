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
package com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.actions.HomeAction
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.seam.actions.Load
import com.savvasdalkitsis.uhuruphotos.feature.home.view.implementation.ui.state.HomeState
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.R.string
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.FullLoading
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scaffold.CommonScaffold

@Composable
internal fun Home(
    state: HomeState,
    action: (HomeAction) -> Unit,
) {
    CommonScaffold {
        if (state.isLoading) {
            FullLoading()
        } else if (state.needsAuthentication) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { action(Load) },
                ) {
                    Text(stringResource(string.authenticate))
                }
            }
        }
    }
}

