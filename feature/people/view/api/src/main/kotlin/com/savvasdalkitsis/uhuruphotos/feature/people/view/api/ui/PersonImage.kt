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
package com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.PersonState
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Thumbnail
import org.jetbrains.compose.resources.painterResource
import uhuruphotos_android.foundation.icons.api.generated.resources.Res.drawable
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_person_1
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_person_2
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_person_3
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_person_4
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_person_5
import uhuruphotos_android.foundation.icons.api.generated.resources.ic_person_6

@Composable
fun PersonImage(
    modifier: Modifier = Modifier,
    shape: Shape,
    personState: PersonState
) {
    if (personState.imageUrl != null) {
        Thumbnail(
            modifier = modifier
                .aspectRatio(1f)
                .clip(shape),
            url = personState.imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = "person image"
        )
    } else {
        val avatars = remember {
            listOf(
                drawable.ic_person_1,
                drawable.ic_person_2,
                drawable.ic_person_3,
                drawable.ic_person_4,
                drawable.ic_person_5,
                drawable.ic_person_6,
            )
        }
        val icon = remember(personState.id) {
            avatars[personState.id % avatars.size]
        }
        Icon(
            modifier = modifier
                .aspectRatio(1f)
                .clip(shape),
            painter = painterResource(icon),
            tint = Color.Unspecified,
            contentDescription = "person image"
        )
    }
}