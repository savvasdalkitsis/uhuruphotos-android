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
package com.savvasdalkitsis.uhuruphotos.autoalbum.seam

import androidx.compose.ui.geometry.Offset
import com.savvasdalkitsis.uhuruphotos.api.people.view.state.Person
import com.savvasdalkitsis.uhuruphotos.photos.api.model.Photo

internal sealed class AutoAlbumAction {
    object SwipeToRefresh : AutoAlbumAction()
    object NavigateBack : AutoAlbumAction()

    data class LoadAlbum(val albumId: Int) : AutoAlbumAction()
    data class SelectedPhoto(
        val photo: Photo,
        val center: Offset,
        val scale: Float,
    ) : AutoAlbumAction()

    data class PersonSelected(val person: Person) : AutoAlbumAction()
}
