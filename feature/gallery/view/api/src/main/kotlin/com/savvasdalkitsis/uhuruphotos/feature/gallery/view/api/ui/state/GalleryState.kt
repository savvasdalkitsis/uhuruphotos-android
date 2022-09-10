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
package com.savvasdalkitsis.uhuruphotos.feature.gallery.view.api.ui.state

import androidx.annotation.StringRes
import com.savvasdalkitsis.uhuruphotos.feature.collage.view.api.ui.state.CollageState
import com.savvasdalkitsis.uhuruphotos.feature.people.view.api.ui.state.Person
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state.Title

data class GalleryState(
    val collageState: CollageState = CollageState(),
    @get:StringRes
    val error: Int? = null,
    val title: Title = Title.Text(""),
    val people: List<Person> = emptyList(),
    val sorting: GallerySorting = GallerySorting.DATE_DESC,
)