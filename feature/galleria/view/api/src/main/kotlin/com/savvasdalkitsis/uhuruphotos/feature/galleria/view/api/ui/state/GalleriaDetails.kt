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
package com.savvasdalkitsis.uhuruphotos.feature.galleria.view.api.ui.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.savvasdalkitsis.uhuruphotos.api.albums.model.Album
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person

data class GalleriaDetails(
    val title: Title = Title.Text(""),
    val albums: List<Album> = emptyList(),
    val people: List<Person> = emptyList(),
)

sealed class Title {
    @Composable
    abstract fun toText(): String

    data class Text(val title: String) : Title() {
        @Composable
        override fun toText(): String = title
    }

    data class Resource(@StringRes val title: Int) : Title() {
        @Composable
        override fun toText(): String = stringResource(title)
    }
}