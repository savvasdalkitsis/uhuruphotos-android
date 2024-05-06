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
package com.savvasdalkitsis.uhuruphotos.feature.settings.view.implementation.ui

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.compose.stringResource
import androidx.work.NetworkType
import com.savvasdalkitsis.uhuruphotos.foundation.strings.api.Res.strings

@Composable
internal fun NetworkType?.friendlyName(): String =  when (this) {
    NetworkType.CONNECTED -> stringResource(strings.any_connected)
    NetworkType.UNMETERED -> stringResource(strings.unmetered)
    NetworkType.NOT_ROAMING -> stringResource(strings.not_roaming)
    NetworkType.METERED -> stringResource(strings.metered)
    else -> "-"
}