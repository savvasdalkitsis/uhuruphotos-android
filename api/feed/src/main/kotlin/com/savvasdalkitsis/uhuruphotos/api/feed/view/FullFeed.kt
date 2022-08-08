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
package com.savvasdalkitsis.uhuruphotos.api.feed.view

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.api.media.page.view.MediaItemSelected
import com.savvasdalkitsis.uhuruphotos.api.media.page.view.MediaItemThumbnail
import com.savvasdalkitsis.uhuruphotos.api.ui.view.LazyStaggeredGrid

@Composable
fun FullFeed(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    albums: List<Album>,
    onMediaItemSelected: MediaItemSelected,
) {
    LazyStaggeredGrid(
        modifier = modifier
            .padding(start = 1.dp, end = 1.dp),
        columnCount = when (LocalConfiguration.current.orientation) {
            ORIENTATION_LANDSCAPE -> 5
            else -> 2
        },
        contentPadding = contentPadding,
    ) {
        albums.flatMap { it.photos }.forEach { photo ->
            item(key = photo.id) {
                MediaItemThumbnail(
                    modifier = Modifier.fillMaxSize(),
                    mediaItem = photo,
                    onItemSelected = onMediaItemSelected
                )
            }
        }
    }
}