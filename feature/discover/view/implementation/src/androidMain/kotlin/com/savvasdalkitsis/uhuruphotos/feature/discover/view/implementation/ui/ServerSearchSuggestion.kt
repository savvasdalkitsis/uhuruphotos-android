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
package com.savvasdalkitsis.uhuruphotos.feature.discover.view.implementation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.painterResource
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images

internal fun LazyListScope.serverSearchSuggestion(
    suggestion: String,
    onClick: () -> Unit,
) {
    item("server-$suggestion", contentType = "serverSearchSuggestion") {
        Suggestion(
            modifier = Modifier.animateItemPlacement(),
            text = suggestion,
            onClick = onClick,
            leadingContent = {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    painter = painterResource(images.ic_assistant),
                    contentDescription = null
                )
            }
        )
    }
}