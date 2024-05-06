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
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import dev.icerock.moko.resources.compose.painterResource
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.R
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res
import com.savvasdalkitsis.uhuruphotos.foundation.icons.api.Res.images
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.Thumbnail

@Composable
fun PersonImage(
    modifier: Modifier = Modifier,
    shape: Shape,
    person: Person
) {
    if (person.imageUrl != null) {
        Thumbnail(
            modifier = modifier
                .aspectRatio(1f)
                .clip(shape),
            url = person.imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = "person image"
        )
    } else {
        val avatars = remember {
            listOf(
                images.ic_person_1,
                images.ic_person_2,
                images.ic_person_3,
                images.ic_person_4,
                images.ic_person_5,
                images.ic_person_6,
            )
        }
        val icon = remember(person.id) {
            avatars[person.id % avatars.size]
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