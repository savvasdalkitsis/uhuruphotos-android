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
package com.savvasdalkitsis.uhuruphotos.people.api.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.image.api.view.Image
import com.savvasdalkitsis.uhuruphotos.people.api.view.state.Person

@Composable
fun PersonThumbnail(
    modifier: Modifier = Modifier,
    person: Person,
    onPersonSelected: () -> Unit = {},
    shape: RoundedCornerShape = CircleShape,
) {
    Column(
        modifier = modifier.clickable { onPersonSelected() },
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .clip(shape),
            url = person.imageUrl,
            contentScale = ContentScale.Crop,
            contentDescription = "person image"
        )
        Text(
            text = person.name,
            maxLines = 2,
            textAlign = TextAlign.Center,
        )
    }

}